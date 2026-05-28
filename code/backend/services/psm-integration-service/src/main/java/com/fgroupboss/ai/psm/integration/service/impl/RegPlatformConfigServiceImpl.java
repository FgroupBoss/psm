package com.fgroupboss.ai.psm.integration.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.integration.mapper.RegPlatformConfigMapper;
import com.fgroupboss.ai.psm.integration.model.dto.RegPlatformConfigRequest;
import com.fgroupboss.ai.psm.integration.model.entity.RegPlatformConfigEntity;
import com.fgroupboss.ai.psm.integration.model.vo.RegPlatformConfigVO;
import com.fgroupboss.ai.psm.integration.service.RegPlatformConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegPlatformConfigServiceImpl implements RegPlatformConfigService {

    private final RegPlatformConfigMapper platformConfigMapper;

    @Override
    public PageResult<RegPlatformConfigVO> page(Long tenantId, String keyword, Integer enabled,
                                                int pageNo, int pageSize) {
        requireTenantId(tenantId);
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);

        LambdaQueryWrapper<RegPlatformConfigEntity> wrapper = baseWrapper(tenantId);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(RegPlatformConfigEntity::getPlatformCode, keyword.trim())
                    .or()
                    .like(RegPlatformConfigEntity::getPlatformName, keyword.trim()));
        }
        if (enabled != null) {
            wrapper.eq(RegPlatformConfigEntity::getEnabled, enabled);
        }
        wrapper.orderByDesc(RegPlatformConfigEntity::getId);

        Page<RegPlatformConfigEntity> mpPage = new Page<RegPlatformConfigEntity>(normalizedPageNo, normalizedPageSize);
        Page<RegPlatformConfigEntity> result = platformConfigMapper.selectPage(mpPage, wrapper);

        List<RegPlatformConfigVO> records = new ArrayList<RegPlatformConfigVO>();
        for (RegPlatformConfigEntity entity : result.getRecords()) {
            records.add(toVO(entity));
        }
        return new PageResult<RegPlatformConfigVO>(result.getTotal(), normalizedPageNo, normalizedPageSize, records);
    }

    @Override
    public List<RegPlatformConfigVO> list(Long tenantId, Integer enabled) {
        requireTenantId(tenantId);
        LambdaQueryWrapper<RegPlatformConfigEntity> wrapper = baseWrapper(tenantId);
        if (enabled != null) {
            wrapper.eq(RegPlatformConfigEntity::getEnabled, enabled);
        }
        wrapper.orderByAsc(RegPlatformConfigEntity::getPlatformCode);
        List<RegPlatformConfigVO> list = new ArrayList<RegPlatformConfigVO>();
        for (RegPlatformConfigEntity entity : platformConfigMapper.selectList(wrapper)) {
            list.add(toVO(entity));
        }
        return list;
    }

    @Override
    @Transactional
    public RegPlatformConfigVO save(RegPlatformConfigRequest request) {
        requireTenantId(request.getTenantId());
        String platformCode = request.getPlatformCode().trim();

        RegPlatformConfigEntity existing = findByCode(request.getTenantId(), platformCode);
        RegPlatformConfigEntity entity = existing == null ? new RegPlatformConfigEntity() : existing;
        entity.setTenantId(request.getTenantId());
        entity.setPlatformCode(platformCode);
        entity.setPlatformName(request.getPlatformName().trim());
        entity.setBaseUrl(request.getBaseUrl().trim());
        entity.setAuthType(StringUtils.hasText(request.getAuthType()) ? request.getAuthType().trim() : "TOKEN");
        if (StringUtils.hasText(request.getCredentialRef())) {
            entity.setCredentialRef(request.getCredentialRef().trim());
        }
        entity.setEnabled(request.getEnabled() == null ? 1 : request.getEnabled());
        entity.setRemark(request.getRemark());
        if (existing == null) {
            entity.setDeleted(0);
            platformConfigMapper.insert(entity);
        } else {
            platformConfigMapper.updateById(entity);
        }
        return toVO(entity);
    }

    @Override
    public RegPlatformConfigVO requireEnabledPlatform(Long tenantId, String platformCode) {
        RegPlatformConfigEntity entity = requirePlatformEntity(tenantId, platformCode);
        if (entity.getEnabled() == null || entity.getEnabled() != 1) {
            throw new BusinessException(400, "platform is disabled: " + platformCode);
        }
        return toVO(entity);
    }

    @Override
    public void requirePlatform(Long tenantId, String platformCode) {
        requirePlatformEntity(tenantId, platformCode);
    }

    @Override
    public List<RegPlatformConfigVO> listAllEnabled() {
        LambdaQueryWrapper<RegPlatformConfigEntity> wrapper = new LambdaQueryWrapper<RegPlatformConfigEntity>();
        wrapper.eq(RegPlatformConfigEntity::getDeleted, 0);
        wrapper.eq(RegPlatformConfigEntity::getEnabled, 1);
        wrapper.orderByAsc(RegPlatformConfigEntity::getTenantId);
        wrapper.orderByAsc(RegPlatformConfigEntity::getPlatformCode);
        List<RegPlatformConfigVO> list = new ArrayList<RegPlatformConfigVO>();
        for (RegPlatformConfigEntity entity : platformConfigMapper.selectList(wrapper)) {
            list.add(toVO(entity));
        }
        return list;
    }

    private RegPlatformConfigEntity requirePlatformEntity(Long tenantId, String platformCode) {
        RegPlatformConfigEntity entity = findByCode(tenantId, platformCode);
        if (entity == null) {
            throw new BusinessException(404, "platform config not found: " + platformCode);
        }
        return entity;
    }

    private RegPlatformConfigEntity findByCode(Long tenantId, String platformCode) {
        LambdaQueryWrapper<RegPlatformConfigEntity> wrapper = baseWrapper(tenantId);
        wrapper.eq(RegPlatformConfigEntity::getPlatformCode, platformCode.trim());
        return platformConfigMapper.selectOne(wrapper);
    }

    private LambdaQueryWrapper<RegPlatformConfigEntity> baseWrapper(Long tenantId) {
        LambdaQueryWrapper<RegPlatformConfigEntity> wrapper = new LambdaQueryWrapper<RegPlatformConfigEntity>();
        wrapper.eq(RegPlatformConfigEntity::getTenantId, tenantId);
        wrapper.eq(RegPlatformConfigEntity::getDeleted, 0);
        return wrapper;
    }

    private RegPlatformConfigVO toVO(RegPlatformConfigEntity entity) {
        RegPlatformConfigVO vo = new RegPlatformConfigVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setPlatformCode(entity.getPlatformCode());
        vo.setPlatformName(entity.getPlatformName());
        vo.setBaseUrl(entity.getBaseUrl());
        vo.setAuthType(entity.getAuthType());
        vo.setCredentialRef(entity.getCredentialRef());
        vo.setEnabled(entity.getEnabled());
        vo.setRemark(entity.getRemark());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is invalid");
        }
    }
}
