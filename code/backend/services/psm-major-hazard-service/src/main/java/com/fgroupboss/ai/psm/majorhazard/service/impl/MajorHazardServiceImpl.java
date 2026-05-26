package com.fgroupboss.ai.psm.majorhazard.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardMapper;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardEntity;
import com.fgroupboss.ai.psm.majorhazard.model.vo.MajorHazardVO;
import com.fgroupboss.ai.psm.majorhazard.service.MajorHazardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MajorHazardServiceImpl implements MajorHazardService {

    private final MajorHazardMapper hazardMapper;

    @Override
    public PageResult<MajorHazardVO> page(Long tenantId, String keyword, String status, String level,
                                          int pageNo, int pageSize) {
        requireTenantId(tenantId);
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (normalizedPageNo - 1) * normalizedPageSize;
        String normalizedKeyword = normalizeText(keyword);
        String normalizedStatus = normalizeText(status);
        String normalizedLevel = normalizeText(level);
        long total = hazardMapper.countByTenant(tenantId, normalizedKeyword, normalizedStatus, normalizedLevel);
        List<MajorHazardEntity> entities = total == 0
                ? new ArrayList<MajorHazardEntity>()
                : hazardMapper.listByTenant(tenantId, normalizedKeyword, normalizedStatus, normalizedLevel,
                offset, normalizedPageSize);
        List<MajorHazardVO> records = new ArrayList<MajorHazardVO>();
        for (MajorHazardEntity entity : entities) {
            records.add(toVO(entity));
        }
        return new PageResult<MajorHazardVO>(total, normalizedPageNo, normalizedPageSize, records);
    }

    @Override
    public MajorHazardVO getById(Long tenantId, Long id) {
        requireTenantId(tenantId);
        MajorHazardEntity entity = hazardMapper.selectById(id);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "major hazard not found");
        }
        return toVO(entity);
    }

    private MajorHazardVO toVO(MajorHazardEntity entity) {
        MajorHazardVO vo = new MajorHazardVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setHazardNo(entity.getHazardNo());
        vo.setName(entity.getName());
        vo.setHazardType(entity.getHazardType());
        vo.setLevel(entity.getLevel());
        vo.setAreaId(entity.getAreaId());
        vo.setUnitId(entity.getUnitId());
        vo.setMaterial(entity.getMaterial());
        vo.setStatus(entity.getStatus());
        vo.setPublishedAt(entity.getPublishedAt());
        return vo;
    }

    private void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
