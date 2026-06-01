package com.fgroupboss.ai.psm.risk.dualprevention.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.risk.dualprevention.mapper.ControlMeasureMapper;
import com.fgroupboss.ai.psm.risk.dualprevention.mapper.RiskEventMapper;
import com.fgroupboss.ai.psm.risk.dualprevention.mapper.RiskUnitMapper;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.ControlMeasureRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.RiskEventRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.entity.ControlMeasureEntity;
import com.fgroupboss.ai.psm.risk.dualprevention.model.entity.RiskEventEntity;
import com.fgroupboss.ai.psm.risk.dualprevention.model.entity.RiskUnitEntity;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.ControlMeasureVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.RiskEventVO;
import com.fgroupboss.ai.psm.risk.dualprevention.service.RiskEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 风险事件与管控措施维护。
 */
@Service
@RequiredArgsConstructor
public class RiskEventServiceImpl implements RiskEventService {

    private final RiskEventMapper riskEventMapper;
    private final RiskUnitMapper riskUnitMapper;
    private final ControlMeasureMapper controlMeasureMapper;

    @Override
    @Transactional
    public RiskEventVO update(Long eventId, RiskEventRequest request, String operator) {
        RiskEventEntity entity = requireEvent(request.getTenantId(), eventId);
        requireUnit(request.getTenantId(), entity.getRiskUnitId());
        applyEventRequest(entity, request);
        if (StringUtils.hasText(request.getStatus())) {
            entity.setStatus(request.getStatus().trim());
        }
        riskEventMapper.updateById(entity);
        return toEventVO(entity);
    }

    @Override
    @Transactional
    public void delete(Long tenantId, Long eventId, String operator) {
        RiskEventEntity entity = requireEvent(tenantId, eventId);
        entity.setDeleted(1);
        riskEventMapper.updateById(entity);
    }

    @Override
    public List<ControlMeasureVO> listMeasures(Long tenantId, Long eventId) {
        requireEvent(tenantId, eventId);
        List<ControlMeasureEntity> entities = controlMeasureMapper.selectList(
                new LambdaQueryWrapper<ControlMeasureEntity>()
                        .eq(ControlMeasureEntity::getTenantId, tenantId)
                        .eq(ControlMeasureEntity::getRiskEventId, eventId)
                        .eq(ControlMeasureEntity::getDeleted, 0)
                        .orderByAsc(ControlMeasureEntity::getId));
        List<ControlMeasureVO> records = new ArrayList<ControlMeasureVO>();
        for (ControlMeasureEntity entity : entities) {
            records.add(toMeasureVO(entity));
        }
        return records;
    }

    @Override
    @Transactional
    public ControlMeasureVO createMeasure(Long eventId, ControlMeasureRequest request, String operator) {
        requireEvent(request.getTenantId(), eventId);
        ControlMeasureEntity entity = new ControlMeasureEntity();
        entity.setTenantId(request.getTenantId());
        entity.setRiskEventId(eventId);
        applyMeasureRequest(entity, request);
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus().trim() : "ACTIVE");
        entity.setDeleted(0);
        controlMeasureMapper.insert(entity);
        return toMeasureVO(entity);
    }

    private void applyEventRequest(RiskEventEntity entity, RiskEventRequest request) {
        entity.setEventCode(request.getEventCode().trim());
        entity.setEventName(request.getEventName().trim());
        entity.setHazardFactors(request.getHazardFactors());
        entity.setPossibleConsequence(request.getPossibleConsequence());
        entity.setInherentRiskLevel(request.getInherentRiskLevel());
    }

    private void applyMeasureRequest(ControlMeasureEntity entity, ControlMeasureRequest request) {
        entity.setMeasureType(request.getMeasureType().trim());
        entity.setMeasureContent(request.getMeasureContent().trim());
        entity.setResponsiblePost(request.getResponsiblePost());
        entity.setCheckCycleDays(request.getCheckCycleDays());
    }

    private RiskUnitEntity requireUnit(Long tenantId, Long unitId) {
        RiskUnitEntity entity = riskUnitMapper.selectById(unitId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "risk unit not found");
        }
        return entity;
    }

    private RiskEventEntity requireEvent(Long tenantId, Long eventId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
        RiskEventEntity entity = riskEventMapper.selectById(eventId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "risk event not found");
        }
        return entity;
    }

    private RiskEventVO toEventVO(RiskEventEntity entity) {
        RiskEventVO vo = new RiskEventVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setRiskUnitId(entity.getRiskUnitId());
        vo.setEventCode(entity.getEventCode());
        vo.setEventName(entity.getEventName());
        vo.setHazardFactors(entity.getHazardFactors());
        vo.setPossibleConsequence(entity.getPossibleConsequence());
        vo.setInherentRiskLevel(entity.getInherentRiskLevel());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    private ControlMeasureVO toMeasureVO(ControlMeasureEntity entity) {
        ControlMeasureVO vo = new ControlMeasureVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setRiskEventId(entity.getRiskEventId());
        vo.setMeasureType(entity.getMeasureType());
        vo.setMeasureContent(entity.getMeasureContent());
        vo.setResponsiblePost(entity.getResponsiblePost());
        vo.setCheckCycleDays(entity.getCheckCycleDays());
        vo.setStatus(entity.getStatus());
        return vo;
    }
}
