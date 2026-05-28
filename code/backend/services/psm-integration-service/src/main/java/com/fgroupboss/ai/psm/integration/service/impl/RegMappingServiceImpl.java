package com.fgroupboss.ai.psm.integration.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.integration.mapper.RegCodeMappingMapper;
import com.fgroupboss.ai.psm.integration.mapper.RegFieldMappingMapper;
import com.fgroupboss.ai.psm.integration.model.dto.RegMappingSaveRequest;
import com.fgroupboss.ai.psm.integration.model.entity.RegCodeMappingEntity;
import com.fgroupboss.ai.psm.integration.model.entity.RegFieldMappingEntity;
import com.fgroupboss.ai.psm.integration.model.vo.RegMappingVO;
import com.fgroupboss.ai.psm.integration.service.RegMappingService;
import com.fgroupboss.ai.psm.integration.service.RegPlatformConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegMappingServiceImpl implements RegMappingService {

    private final RegFieldMappingMapper fieldMappingMapper;
    private final RegCodeMappingMapper codeMappingMapper;
    private final RegPlatformConfigService platformConfigService;

    @Override
    public RegMappingVO list(Long tenantId, String platformCode, String dataDomain) {
        requireTenantId(tenantId);
        if (!StringUtils.hasText(platformCode)) {
            throw new BusinessException(400, "platformCode is required");
        }
        String code = platformCode.trim();

        LambdaQueryWrapper<RegFieldMappingEntity> fieldWrapper = fieldBaseWrapper(tenantId, code);
        if (StringUtils.hasText(dataDomain)) {
            fieldWrapper.eq(RegFieldMappingEntity::getDataDomain, dataDomain.trim());
        }
        fieldWrapper.orderByAsc(RegFieldMappingEntity::getId);

        LambdaQueryWrapper<RegCodeMappingEntity> codeWrapper = codeBaseWrapper(tenantId, code);
        codeWrapper.orderByAsc(RegCodeMappingEntity::getId);

        RegMappingVO vo = new RegMappingVO();
        vo.setFieldMappings(toFieldVOList(fieldMappingMapper.selectList(fieldWrapper)));
        vo.setCodeMappings(toCodeVOList(codeMappingMapper.selectList(codeWrapper)));
        return vo;
    }

    @Override
    @Transactional
    public RegMappingVO save(RegMappingSaveRequest request) {
        requireTenantId(request.getTenantId());
        platformConfigService.requirePlatform(request.getTenantId(), request.getPlatformCode());
        String platformCode = request.getPlatformCode().trim();

        if (!CollectionUtils.isEmpty(request.getFieldMappings())) {
            for (RegMappingSaveRequest.RegFieldMappingItem item : request.getFieldMappings()) {
                upsertFieldMapping(request.getTenantId(), platformCode, item);
            }
        }
        if (!CollectionUtils.isEmpty(request.getCodeMappings())) {
            for (RegMappingSaveRequest.RegCodeMappingItem item : request.getCodeMappings()) {
                upsertCodeMapping(request.getTenantId(), platformCode, item);
            }
        }
        return list(request.getTenantId(), platformCode, null);
    }

    private void upsertFieldMapping(Long tenantId, String platformCode,
                                    RegMappingSaveRequest.RegFieldMappingItem item) {
        RegFieldMappingEntity entity;
        if (item.getId() != null) {
            entity = fieldMappingMapper.selectById(item.getId());
            if (entity == null || !tenantId.equals(entity.getTenantId()) || entity.getDeleted() != 0) {
                throw new BusinessException(404, "field mapping not found: " + item.getId());
            }
        } else {
            entity = new RegFieldMappingEntity();
            entity.setTenantId(tenantId);
            entity.setPlatformCode(platformCode);
            entity.setDeleted(0);
        }
        entity.setDataDomain(item.getDataDomain().trim());
        entity.setSourceField(item.getSourceField().trim());
        entity.setTargetField(item.getTargetField().trim());
        entity.setTransformRule(item.getTransformRule());
        entity.setEnabled(item.getEnabled() == null ? 1 : item.getEnabled());
        if (item.getId() == null) {
            fieldMappingMapper.insert(entity);
        } else {
            fieldMappingMapper.updateById(entity);
        }
    }

    private void upsertCodeMapping(Long tenantId, String platformCode,
                                   RegMappingSaveRequest.RegCodeMappingItem item) {
        RegCodeMappingEntity entity;
        if (item.getId() != null) {
            entity = codeMappingMapper.selectById(item.getId());
            if (entity == null || !tenantId.equals(entity.getTenantId()) || entity.getDeleted() != 0) {
                throw new BusinessException(404, "code mapping not found: " + item.getId());
            }
        } else {
            entity = new RegCodeMappingEntity();
            entity.setTenantId(tenantId);
            entity.setPlatformCode(platformCode);
            entity.setDeleted(0);
        }
        entity.setMappingType(item.getMappingType().trim());
        entity.setSourceCode(item.getSourceCode().trim());
        entity.setTargetCode(item.getTargetCode().trim());
        entity.setDescription(item.getDescription());
        entity.setEnabled(item.getEnabled() == null ? 1 : item.getEnabled());
        if (item.getId() == null) {
            codeMappingMapper.insert(entity);
        } else {
            codeMappingMapper.updateById(entity);
        }
    }

    private LambdaQueryWrapper<RegFieldMappingEntity> fieldBaseWrapper(Long tenantId, String platformCode) {
        LambdaQueryWrapper<RegFieldMappingEntity> wrapper = new LambdaQueryWrapper<RegFieldMappingEntity>();
        wrapper.eq(RegFieldMappingEntity::getTenantId, tenantId);
        wrapper.eq(RegFieldMappingEntity::getPlatformCode, platformCode);
        wrapper.eq(RegFieldMappingEntity::getDeleted, 0);
        return wrapper;
    }

    private LambdaQueryWrapper<RegCodeMappingEntity> codeBaseWrapper(Long tenantId, String platformCode) {
        LambdaQueryWrapper<RegCodeMappingEntity> wrapper = new LambdaQueryWrapper<RegCodeMappingEntity>();
        wrapper.eq(RegCodeMappingEntity::getTenantId, tenantId);
        wrapper.eq(RegCodeMappingEntity::getPlatformCode, platformCode);
        wrapper.eq(RegCodeMappingEntity::getDeleted, 0);
        return wrapper;
    }

    private List<RegMappingVO.RegFieldMappingVO> toFieldVOList(List<RegFieldMappingEntity> entities) {
        List<RegMappingVO.RegFieldMappingVO> list = new ArrayList<RegMappingVO.RegFieldMappingVO>();
        for (RegFieldMappingEntity entity : entities) {
            RegMappingVO.RegFieldMappingVO vo = new RegMappingVO.RegFieldMappingVO();
            vo.setId(entity.getId());
            vo.setTenantId(entity.getTenantId());
            vo.setPlatformCode(entity.getPlatformCode());
            vo.setDataDomain(entity.getDataDomain());
            vo.setSourceField(entity.getSourceField());
            vo.setTargetField(entity.getTargetField());
            vo.setTransformRule(entity.getTransformRule());
            vo.setEnabled(entity.getEnabled());
            list.add(vo);
        }
        return list;
    }

    private List<RegMappingVO.RegCodeMappingVO> toCodeVOList(List<RegCodeMappingEntity> entities) {
        List<RegMappingVO.RegCodeMappingVO> list = new ArrayList<RegMappingVO.RegCodeMappingVO>();
        for (RegCodeMappingEntity entity : entities) {
            RegMappingVO.RegCodeMappingVO vo = new RegMappingVO.RegCodeMappingVO();
            vo.setId(entity.getId());
            vo.setTenantId(entity.getTenantId());
            vo.setPlatformCode(entity.getPlatformCode());
            vo.setMappingType(entity.getMappingType());
            vo.setSourceCode(entity.getSourceCode());
            vo.setTargetCode(entity.getTargetCode());
            vo.setDescription(entity.getDescription());
            vo.setEnabled(entity.getEnabled());
            list.add(vo);
        }
        return list;
    }

    private void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is invalid");
        }
    }
}
