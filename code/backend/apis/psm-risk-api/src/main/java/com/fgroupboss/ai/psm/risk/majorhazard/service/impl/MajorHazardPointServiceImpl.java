package com.fgroupboss.ai.psm.risk.majorhazard.service.impl;

import com.fgroupboss.ai.psm.common.AuditBizType;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.risk.majorhazard.mapper.MajorHazardMapper;
import com.fgroupboss.ai.psm.risk.majorhazard.mapper.MajorHazardPointRelMapper;
import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.HazardPointRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.entity.MajorHazardEntity;
import com.fgroupboss.ai.psm.risk.majorhazard.model.entity.MajorHazardPointRelEntity;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.HazardPointVO;
import com.fgroupboss.ai.psm.risk.majorhazard.service.MajorHazardPointService;
import com.fgroupboss.ai.psm.risk.majorhazard.support.MajorHazardAuditSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现方式：承载危险源点位业务实现，基于 Mapper、远程客户端或支撑组件完成校验、状态流转和结果组装。
 */
@Service
@RequiredArgsConstructor
public class MajorHazardPointServiceImpl implements MajorHazardPointService {

    private final MajorHazardMapper hazardMapper;
    private final MajorHazardPointRelMapper pointRelMapper;
    private final MajorHazardAuditSupport auditSupport;

    /**
     * 实现方式：查询关联点位，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<HazardPointVO> listPoints(Long tenantId, Long hazardId) {
        requireHazard(tenantId, hazardId);
        List<MajorHazardPointRelEntity> entities = pointRelMapper.listByHazardId(tenantId, hazardId);
        List<HazardPointVO> records = new ArrayList<HazardPointVO>();
        for (MajorHazardPointRelEntity entity : entities) {
            records.add(toVO(entity));
        }
        return records;
    }

    /**
     * 实现方式：绑定点位，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public HazardPointVO bindPoint(Long tenantId, Long hazardId, HazardPointRequest request, String operator) {
        requireHazard(tenantId, hazardId);
        assertMonitorPointId(request.getMonitorPointId());
        MajorHazardPointRelEntity existing = pointRelMapper.findActive(tenantId, hazardId, request.getMonitorPointId());
        if (existing != null) {
            throw new BusinessException(400, "monitor point already bound to hazard");
        }
        MajorHazardPointRelEntity entity = new MajorHazardPointRelEntity();
        entity.setTenantId(tenantId);
        entity.setHazardId(hazardId);
        entity.setMonitorPointId(request.getMonitorPointId());
        entity.setPointCode(normalizeText(request.getPointCode()));
        entity.setPointName(normalizeText(request.getPointName()));
        entity.setDeleted(0);
        pointRelMapper.insert(entity);
        auditSupport.write(tenantId, hazardId, AuditBizType.MAJOR_HAZARD_POINT.name(), "BIND",
                "绑定点位 " + request.getMonitorPointId(), operator);
        return toVO(entity);
    }

    /**
     * 实现方式：解绑点位，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void unbindPoint(Long tenantId, Long hazardId, Long relId, String operator) {
        requireHazard(tenantId, hazardId);
        MajorHazardPointRelEntity entity = pointRelMapper.selectById(relId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId()) || !hazardId.equals(entity.getHazardId())) {
            throw new BusinessException(404, "hazard point relation not found");
        }
        entity.setDeleted(1);
        pointRelMapper.updateById(entity);
        auditSupport.write(tenantId, hazardId, AuditBizType.MAJOR_HAZARD_POINT.name(), "UNBIND",
                "解绑点位 " + entity.getMonitorPointId(), operator);
    }

    private MajorHazardEntity requireHazard(Long tenantId, Long hazardId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
        MajorHazardEntity entity = hazardMapper.selectById(hazardId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "major hazard not found");
        }
        return entity;
    }

    private void assertMonitorPointId(Long monitorPointId) {
        if (monitorPointId == null || monitorPointId <= 0) {
            throw new BusinessException(400, "monitorPointId is required");
        }
    }

    private HazardPointVO toVO(MajorHazardPointRelEntity entity) {
        HazardPointVO vo = new HazardPointVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setHazardId(entity.getHazardId());
        vo.setMonitorPointId(entity.getMonitorPointId());
        vo.setPointCode(entity.getPointCode());
        vo.setPointName(entity.getPointName());
        return vo;
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
