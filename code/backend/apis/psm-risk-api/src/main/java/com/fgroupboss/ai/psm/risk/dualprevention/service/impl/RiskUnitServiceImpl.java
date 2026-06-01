package com.fgroupboss.ai.psm.risk.dualprevention.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.risk.dualprevention.config.RiskUnitStatus;
import com.fgroupboss.ai.psm.risk.dualprevention.mapper.ControlMeasureMapper;
import com.fgroupboss.ai.psm.risk.dualprevention.mapper.RiskEventMapper;
import com.fgroupboss.ai.psm.risk.dualprevention.mapper.RiskUnitMapper;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.RiskEventRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.RiskUnitRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.entity.ControlMeasureEntity;
import com.fgroupboss.ai.psm.risk.dualprevention.model.entity.RiskEventEntity;
import com.fgroupboss.ai.psm.risk.dualprevention.model.entity.RiskUnitEntity;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.ControlMeasureVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.RiskColorStatVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.RiskEventTreeNodeVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.RiskEventVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.RiskUnitTreeNodeVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.RiskUnitVO;
import com.fgroupboss.ai.psm.risk.dualprevention.service.RiskUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 风险单元与单元下风险事件维护。
 */
@Service
@RequiredArgsConstructor
public class RiskUnitServiceImpl implements RiskUnitService {

    private final RiskUnitMapper riskUnitMapper;
    private final RiskEventMapper riskEventMapper;
    private final ControlMeasureMapper controlMeasureMapper;

    @Override
    public PageResult<RiskUnitVO> page(Long tenantId, String keyword, String status, Long areaId,
                                       int pageNo, int pageSize) {
        requireTenantId(tenantId);
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);

        LambdaQueryWrapper<RiskUnitEntity> wrapper = baseUnitWrapper(tenantId);
        applyUnitFilters(wrapper, keyword, status, areaId);

        Page<RiskUnitEntity> mpPage = new Page<RiskUnitEntity>(normalizedPageNo, normalizedPageSize);
        Page<RiskUnitEntity> result = riskUnitMapper.selectPage(mpPage, wrapper);

        List<RiskUnitVO> records = new ArrayList<RiskUnitVO>();
        for (RiskUnitEntity entity : result.getRecords()) {
            records.add(toUnitVO(entity));
        }
        return new PageResult<RiskUnitVO>(result.getTotal(), normalizedPageNo, normalizedPageSize, records);
    }

    @Override
    public RiskUnitVO getById(Long tenantId, Long id) {
        return toUnitVO(requireUnit(tenantId, id));
    }

    @Override
    @Transactional
    public RiskUnitVO create(RiskUnitRequest request, String operator) {
        requireTenantId(request.getTenantId());
        assertUnitCodeUnique(request.getTenantId(), request.getUnitCode(), null);
        if (request.getParentId() != null) {
            requireUnit(request.getTenantId(), request.getParentId());
        }

        RiskUnitEntity entity = new RiskUnitEntity();
        entity.setTenantId(request.getTenantId());
        applyUnitRequest(entity, request);
        entity.setStatus(resolveUnitStatus(request.getStatus(), RiskUnitStatus.DRAFT.name()));
        entity.setDeleted(0);
        riskUnitMapper.insert(entity);
        return toUnitVO(entity);
    }

    @Override
    @Transactional
    public RiskUnitVO update(Long id, RiskUnitRequest request, String operator) {
        RiskUnitEntity entity = requireUnit(request.getTenantId(), id);
        RiskUnitStatus.assertEditable(entity.getStatus());
        assertUnitCodeUnique(request.getTenantId(), request.getUnitCode(), id);
        applyUnitRequest(entity, request);
        if (StringUtils.hasText(request.getStatus())) {
            entity.setStatus(request.getStatus().trim());
        }
        riskUnitMapper.updateById(entity);
        return toUnitVO(entity);
    }

    @Override
    @Transactional
    public void delete(Long tenantId, Long id, String operator) {
        RiskUnitEntity entity = requireUnit(tenantId, id);
        entity.setDeleted(1);
        riskUnitMapper.updateById(entity);
    }

    @Override
    public List<RiskUnitTreeNodeVO> tree(Long tenantId, Long areaId) {
        requireTenantId(tenantId);
        LambdaQueryWrapper<RiskUnitEntity> unitWrapper = baseUnitWrapper(tenantId);
        if (areaId != null) {
            unitWrapper.eq(RiskUnitEntity::getAreaId, areaId);
        }
        unitWrapper.orderByAsc(RiskUnitEntity::getId);
        List<RiskUnitEntity> units = riskUnitMapper.selectList(unitWrapper);
        if (units.isEmpty()) {
            return new ArrayList<RiskUnitTreeNodeVO>();
        }

        List<Long> unitIds = new ArrayList<Long>();
        for (RiskUnitEntity unit : units) {
            unitIds.add(unit.getId());
        }

        List<RiskEventEntity> events = riskEventMapper.selectList(
                new LambdaQueryWrapper<RiskEventEntity>()
                        .eq(RiskEventEntity::getTenantId, tenantId)
                        .eq(RiskEventEntity::getDeleted, 0)
                        .in(RiskEventEntity::getRiskUnitId, unitIds)
                        .orderByAsc(RiskEventEntity::getId));

        List<Long> eventIds = new ArrayList<Long>();
        for (RiskEventEntity event : events) {
            eventIds.add(event.getId());
        }

        Map<Long, List<ControlMeasureVO>> measuresByEvent = loadMeasuresByEvent(tenantId, eventIds);
        Map<Long, List<RiskEventTreeNodeVO>> eventsByUnit = groupEvents(events, measuresByEvent);
        return buildUnitTree(units, eventsByUnit);
    }

    @Override
    public List<RiskEventVO> listEvents(Long tenantId, Long unitId) {
        requireUnit(tenantId, unitId);
        List<RiskEventEntity> entities = riskEventMapper.selectList(
                new LambdaQueryWrapper<RiskEventEntity>()
                        .eq(RiskEventEntity::getTenantId, tenantId)
                        .eq(RiskEventEntity::getRiskUnitId, unitId)
                        .eq(RiskEventEntity::getDeleted, 0)
                        .orderByAsc(RiskEventEntity::getId));
        List<RiskEventVO> records = new ArrayList<RiskEventVO>();
        for (RiskEventEntity entity : entities) {
            records.add(toEventVO(entity));
        }
        return records;
    }

    @Override
    public Map<String, Long> colorMap(Long tenantId, Long areaId) {
        Map<String, Long> map = new LinkedHashMap<String, Long>();
        for (RiskColorStatVO stat : colorStats(tenantId, areaId)) {
            map.put(stat.getLevel(), stat.getCount());
        }
        return map;
    }

    @Override
    public List<RiskColorStatVO> colorStats(Long tenantId, Long areaId) {
        requireTenantId(tenantId);
        LambdaQueryWrapper<RiskUnitEntity> wrapper = baseUnitWrapper(tenantId)
                .eq(RiskUnitEntity::getStatus, RiskUnitStatus.ACTIVE.name());
        if (areaId != null) {
            wrapper.eq(RiskUnitEntity::getAreaId, areaId);
        }
        List<RiskUnitEntity> units = riskUnitMapper.selectList(wrapper);
        Map<String, Long> counts = new LinkedHashMap<String, Long>();
        for (RiskUnitEntity unit : units) {
            String level = resolveColorLevel(unit);
            if (!StringUtils.hasText(level)) {
                continue;
            }
            String key = level.trim();
            Long current = counts.get(key);
            counts.put(key, current == null ? 1L : current + 1L);
        }
        List<RiskColorStatVO> stats = new ArrayList<RiskColorStatVO>();
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            RiskColorStatVO stat = new RiskColorStatVO();
            stat.setLevel(entry.getKey());
            stat.setCount(entry.getValue());
            stats.add(stat);
        }
        return stats;
    }

    private String resolveColorLevel(RiskUnitEntity unit) {
        if (StringUtils.hasText(unit.getResidualRiskLevel())) {
            return unit.getResidualRiskLevel();
        }
        return unit.getInherentRiskLevel();
    }

    @Override
    @Transactional
    public RiskEventVO createEvent(Long unitId, RiskEventRequest request, String operator) {
        requireUnit(request.getTenantId(), unitId);
        RiskEventEntity entity = new RiskEventEntity();
        entity.setTenantId(request.getTenantId());
        entity.setRiskUnitId(unitId);
        applyEventRequest(entity, request);
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus().trim() : "ACTIVE");
        entity.setDeleted(0);
        riskEventMapper.insert(entity);
        return toEventVO(entity);
    }

    private Map<Long, List<ControlMeasureVO>> loadMeasuresByEvent(Long tenantId, List<Long> eventIds) {
        Map<Long, List<ControlMeasureVO>> measuresByEvent = new HashMap<Long, List<ControlMeasureVO>>();
        if (eventIds.isEmpty()) {
            return measuresByEvent;
        }
        List<ControlMeasureEntity> measures = controlMeasureMapper.selectList(
                new LambdaQueryWrapper<ControlMeasureEntity>()
                        .eq(ControlMeasureEntity::getTenantId, tenantId)
                        .eq(ControlMeasureEntity::getDeleted, 0)
                        .in(ControlMeasureEntity::getRiskEventId, eventIds)
                        .orderByAsc(ControlMeasureEntity::getId));
        for (ControlMeasureEntity measure : measures) {
            List<ControlMeasureVO> bucket = measuresByEvent.get(measure.getRiskEventId());
            if (bucket == null) {
                bucket = new ArrayList<ControlMeasureVO>();
                measuresByEvent.put(measure.getRiskEventId(), bucket);
            }
            bucket.add(toMeasureVO(measure));
        }
        return measuresByEvent;
    }

    private Map<Long, List<RiskEventTreeNodeVO>> groupEvents(List<RiskEventEntity> events,
                                                             Map<Long, List<ControlMeasureVO>> measuresByEvent) {
        Map<Long, List<RiskEventTreeNodeVO>> eventsByUnit = new HashMap<Long, List<RiskEventTreeNodeVO>>();
        for (RiskEventEntity event : events) {
            List<RiskEventTreeNodeVO> bucket = eventsByUnit.get(event.getRiskUnitId());
            if (bucket == null) {
                bucket = new ArrayList<RiskEventTreeNodeVO>();
                eventsByUnit.put(event.getRiskUnitId(), bucket);
            }
            RiskEventTreeNodeVO node = new RiskEventTreeNodeVO();
            node.setId(event.getId());
            node.setEventCode(event.getEventCode());
            node.setEventName(event.getEventName());
            node.setInherentRiskLevel(event.getInherentRiskLevel());
            node.setStatus(event.getStatus());
            List<ControlMeasureVO> measures = measuresByEvent.get(event.getId());
            if (measures != null) {
                node.setMeasures(measures);
            }
            bucket.add(node);
        }
        return eventsByUnit;
    }

    private List<RiskUnitTreeNodeVO> buildUnitTree(List<RiskUnitEntity> units,
                                                   Map<Long, List<RiskEventTreeNodeVO>> eventsByUnit) {
        Map<Long, RiskUnitTreeNodeVO> nodeMap = new HashMap<Long, RiskUnitTreeNodeVO>();
        List<RiskUnitTreeNodeVO> roots = new ArrayList<RiskUnitTreeNodeVO>();

        for (RiskUnitEntity unit : units) {
            RiskUnitTreeNodeVO node = toTreeNode(unit);
            List<RiskEventTreeNodeVO> events = eventsByUnit.get(unit.getId());
            if (events != null) {
                node.setEvents(events);
            }
            nodeMap.put(unit.getId(), node);
        }

        for (RiskUnitEntity unit : units) {
            RiskUnitTreeNodeVO node = nodeMap.get(unit.getId());
            if (unit.getParentId() == null || !nodeMap.containsKey(unit.getParentId())) {
                roots.add(node);
            } else {
                nodeMap.get(unit.getParentId()).getChildren().add(node);
            }
        }
        return roots;
    }

    private LambdaQueryWrapper<RiskUnitEntity> baseUnitWrapper(Long tenantId) {
        return new LambdaQueryWrapper<RiskUnitEntity>()
                .eq(RiskUnitEntity::getTenantId, tenantId)
                .eq(RiskUnitEntity::getDeleted, 0);
    }

    private void applyUnitFilters(LambdaQueryWrapper<RiskUnitEntity> wrapper, String keyword,
                                  String status, Long areaId) {
        if (StringUtils.hasText(keyword)) {
            String trimmed = keyword.trim();
            wrapper.and(w -> w.like(RiskUnitEntity::getUnitCode, trimmed)
                    .or()
                    .like(RiskUnitEntity::getUnitName, trimmed));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(RiskUnitEntity::getStatus, status.trim());
        }
        if (areaId != null) {
            wrapper.eq(RiskUnitEntity::getAreaId, areaId);
        }
        wrapper.orderByDesc(RiskUnitEntity::getId);
    }

    private void applyUnitRequest(RiskUnitEntity entity, RiskUnitRequest request) {
        entity.setParentId(request.getParentId());
        entity.setUnitCode(request.getUnitCode().trim());
        entity.setUnitName(request.getUnitName().trim());
        entity.setAreaId(request.getAreaId());
        entity.setEquipmentId(request.getEquipmentId());
        entity.setMajorHazardId(request.getMajorHazardId());
        entity.setInherentRiskLevel(request.getInherentRiskLevel());
        entity.setResidualRiskLevel(request.getResidualRiskLevel());
        entity.setOwnerOrgId(request.getOwnerOrgId());
        entity.setOwnerUserId(request.getOwnerUserId());
    }

    private void applyEventRequest(RiskEventEntity entity, RiskEventRequest request) {
        entity.setEventCode(request.getEventCode().trim());
        entity.setEventName(request.getEventName().trim());
        entity.setHazardFactors(request.getHazardFactors());
        entity.setPossibleConsequence(request.getPossibleConsequence());
        entity.setInherentRiskLevel(request.getInherentRiskLevel());
    }

    private void assertUnitCodeUnique(Long tenantId, String unitCode, Long excludeId) {
        RiskUnitEntity existing = riskUnitMapper.selectOne(
                new LambdaQueryWrapper<RiskUnitEntity>()
                        .eq(RiskUnitEntity::getTenantId, tenantId)
                        .eq(RiskUnitEntity::getUnitCode, unitCode.trim())
                        .eq(RiskUnitEntity::getDeleted, 0)
                        .last("limit 1"));
        if (existing != null && (excludeId == null || !excludeId.equals(existing.getId()))) {
            throw new BusinessException(400, "unit code already exists: " + unitCode);
        }
    }

    private RiskUnitEntity requireUnit(Long tenantId, Long id) {
        requireTenantId(tenantId);
        RiskUnitEntity entity = riskUnitMapper.selectById(id);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "risk unit not found");
        }
        return entity;
    }

    private String resolveUnitStatus(String requested, String defaultStatus) {
        return StringUtils.hasText(requested) ? requested.trim() : defaultStatus;
    }

    private RiskUnitVO toUnitVO(RiskUnitEntity entity) {
        RiskUnitVO vo = new RiskUnitVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setParentId(entity.getParentId());
        vo.setUnitCode(entity.getUnitCode());
        vo.setUnitName(entity.getUnitName());
        vo.setAreaId(entity.getAreaId());
        vo.setEquipmentId(entity.getEquipmentId());
        vo.setMajorHazardId(entity.getMajorHazardId());
        vo.setInherentRiskLevel(entity.getInherentRiskLevel());
        vo.setResidualRiskLevel(entity.getResidualRiskLevel());
        vo.setOwnerOrgId(entity.getOwnerOrgId());
        vo.setOwnerUserId(entity.getOwnerUserId());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    private RiskUnitTreeNodeVO toTreeNode(RiskUnitEntity entity) {
        RiskUnitTreeNodeVO node = new RiskUnitTreeNodeVO();
        node.setId(entity.getId());
        node.setParentId(entity.getParentId());
        node.setUnitCode(entity.getUnitCode());
        node.setUnitName(entity.getUnitName());
        node.setInherentRiskLevel(entity.getInherentRiskLevel());
        node.setResidualRiskLevel(entity.getResidualRiskLevel());
        node.setStatus(entity.getStatus());
        return node;
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

    private void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
    }
}
