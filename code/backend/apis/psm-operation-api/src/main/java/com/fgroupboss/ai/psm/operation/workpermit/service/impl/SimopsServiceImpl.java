package com.fgroupboss.ai.psm.operation.workpermit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.operation.workpermit.config.PermitCheckPoint;
import com.fgroupboss.ai.psm.operation.workpermit.config.SimopsAction;
import com.fgroupboss.ai.psm.operation.workpermit.config.SimopsScanStage;
import com.fgroupboss.ai.psm.operation.workpermit.config.WorkPermitStatus;
import com.fgroupboss.ai.psm.operation.workpermit.mapper.SimopsConflictItemMapper;
import com.fgroupboss.ai.psm.operation.workpermit.mapper.SimopsConflictRuleMapper;
import com.fgroupboss.ai.psm.operation.workpermit.mapper.SimopsCoordinationRecordMapper;
import com.fgroupboss.ai.psm.operation.workpermit.mapper.SimopsScanResultMapper;
import com.fgroupboss.ai.psm.operation.workpermit.mapper.WorkPermitMapper;
import com.fgroupboss.ai.psm.operation.workpermit.model.dto.SimopsConflictRuleRequest;
import com.fgroupboss.ai.psm.operation.workpermit.model.dto.SimopsCoordinateRequest;
import com.fgroupboss.ai.psm.operation.workpermit.model.dto.SimopsScanRequest;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.SimopsConflictItemEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.SimopsConflictRuleEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.SimopsCoordinationRecordEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.SimopsScanResultEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.SimopsAreaStatVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.SimopsConflictItemVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.SimopsConflictReasonVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.SimopsConflictRuleVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.SimopsCoordinationRecordVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.SimopsScanResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.SimopsStatisticsVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.SimopsTypePairStatVO;
import com.fgroupboss.ai.psm.operation.workpermit.service.SimopsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimopsServiceImpl implements SimopsService {

    private static final String CODE_SIMOPS_CONFLICT = "SIMOPS_CONFLICT";
    private static final String AREA_SCOPE_ALL = "ALL";
    private static final String FINAL_PASS = "PASS";
    private static final String COORDINATION_APPROVED = "APPROVED";
    private static final String WARN_REASON_PREFIX = "[提示]";

    private final SimopsConflictRuleMapper ruleMapper;
    private final SimopsScanResultMapper scanResultMapper;
    private final SimopsConflictItemMapper conflictItemMapper;
    private final SimopsCoordinationRecordMapper coordinationRecordMapper;
    private final WorkPermitMapper workPermitMapper;

    @Override
    public List<SimopsConflictRuleVO> listRules(Long tenantId, Boolean enabledOnly) {
        requireTenantId(tenantId);
        LambdaQueryWrapper<SimopsConflictRuleEntity> wrapper = ruleQuery(tenantId);
        if (Boolean.TRUE.equals(enabledOnly)) {
            wrapper.eq(SimopsConflictRuleEntity::getEnabled, 1);
        }
        wrapper.orderByAsc(SimopsConflictRuleEntity::getWorkTypeA)
                .orderByAsc(SimopsConflictRuleEntity::getWorkTypeB);
        List<SimopsConflictRuleVO> result = new ArrayList<SimopsConflictRuleVO>();
        for (SimopsConflictRuleEntity entity : ruleMapper.selectList(wrapper)) {
            result.add(toRuleVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public SimopsConflictRuleVO createRule(SimopsConflictRuleRequest request) {
        SimopsConflictRuleEntity entity = new SimopsConflictRuleEntity();
        applyRuleRequest(entity, request);
        entity.setDeleted(0);
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        ruleMapper.insert(entity);
        return toRuleVO(entity);
    }

    @Override
    @Transactional
    public SimopsConflictRuleVO updateRule(Long id, SimopsConflictRuleRequest request) {
        SimopsConflictRuleEntity entity = requireRule(request.getTenantId(), id);
        applyRuleRequest(entity, request);
        entity.setUpdatedAt(new Date());
        ruleMapper.updateById(entity);
        return toRuleVO(entity);
    }

    @Override
    @Transactional
    public void deleteRule(Long tenantId, Long id) {
        SimopsConflictRuleEntity entity = requireRule(tenantId, id);
        entity.setDeleted(1);
        entity.setUpdatedAt(new Date());
        ruleMapper.updateById(entity);
    }

    @Override
    @Transactional
    public SimopsScanResultVO scan(SimopsScanRequest request) {
        WorkPermitEntity permit = requirePermit(request.getTenantId(), request.getWorkPermitId());
        SimopsScanStage stage = SimopsScanStage.from(request.getScanStage());
        return executeScan(permit, stage);
    }

    @Override
    public PageResult<SimopsScanResultVO> listConflicts(Long tenantId, Long workPermitId, String scanStage,
                                                        int pageNo, int pageSize) {
        requireTenantId(tenantId);
        PageSpec spec = normalizePage(pageNo, pageSize);
        LambdaQueryWrapper<SimopsScanResultEntity> wrapper = new LambdaQueryWrapper<SimopsScanResultEntity>()
                .eq(SimopsScanResultEntity::getTenantId, tenantId)
                .gt(SimopsScanResultEntity::getConflictCount, 0);
        if (workPermitId != null) {
            wrapper.eq(SimopsScanResultEntity::getWorkPermitId, workPermitId);
        }
        if (StringUtils.hasText(scanStage)) {
            wrapper.eq(SimopsScanResultEntity::getScanStage, scanStage.trim().toUpperCase());
        }
        wrapper.orderByDesc(SimopsScanResultEntity::getScannedAt);
        long total = scanResultMapper.selectCount(wrapper);
        List<SimopsScanResultEntity> entities = total == 0
                ? new ArrayList<SimopsScanResultEntity>()
                : scanResultMapper.selectList(wrapper.last("limit " + spec.offset + "," + spec.pageSize));
        List<SimopsScanResultVO> records = new ArrayList<SimopsScanResultVO>();
        for (SimopsScanResultEntity entity : entities) {
            records.add(toScanVO(entity, true));
        }
        return new PageResult<SimopsScanResultVO>(total, spec.pageNo, spec.pageSize, records);
    }

    @Override
    @Transactional
    public SimopsCoordinationRecordVO coordinate(Long scanResultId, SimopsCoordinateRequest request, String operator) {
        SimopsScanResultEntity scan = requireScanResult(request.getTenantId(), scanResultId);
        if (scan.getConflictCount() == null || scan.getConflictCount() == 0) {
            throw new BusinessException(409, "无冲突记录可协调");
        }
        SimopsCoordinationRecordEntity record = new SimopsCoordinationRecordEntity();
        record.setTenantId(request.getTenantId());
        record.setScanResultId(scanResultId);
        record.setWorkPermitId(scan.getWorkPermitId());
        record.setDecision(normalizeRequired(request.getDecision(), "decision").toUpperCase());
        record.setOpinion(normalizeText(request.getOpinion()));
        record.setConditionsText(normalizeText(request.getConditionsText()));
        record.setCoordinatorName(StringUtils.hasText(request.getCoordinatorName())
                ? request.getCoordinatorName().trim() : normalizeOperator(operator));
        record.setCoordinatedAt(new Date());
        coordinationRecordMapper.insert(record);
        if (COORDINATION_APPROVED.equals(record.getDecision())) {
            scan.setFinalAction(FINAL_PASS);
            scan.setPassed(1);
            scan.setSuggestion("协调已通过，可继续提交或许可");
            scanResultMapper.updateById(scan);
        }
        log.info("simops-coordinate scanResultId={} decision={}", scanResultId, record.getDecision());
        return toCoordinationVO(record);
    }

    @Override
    public SimopsStatisticsVO statistics(Long tenantId) {
        requireTenantId(tenantId);
        SimopsStatisticsVO vo = new SimopsStatisticsVO();
        LambdaQueryWrapper<SimopsScanResultEntity> scanWrapper = new LambdaQueryWrapper<SimopsScanResultEntity>()
                .eq(SimopsScanResultEntity::getTenantId, tenantId);
        vo.setTotalScans(scanResultMapper.selectCount(scanWrapper));
        LambdaQueryWrapper<SimopsScanResultEntity> conflictWrapper = new LambdaQueryWrapper<SimopsScanResultEntity>()
                .eq(SimopsScanResultEntity::getTenantId, tenantId)
                .gt(SimopsScanResultEntity::getConflictCount, 0);
        vo.setTotalConflicts(scanResultMapper.selectCount(conflictWrapper));
        vo.setBlockCount(countByFinalAction(tenantId, SimopsAction.BLOCK.name()));
        vo.setCoordinateCount(countByFinalAction(tenantId, SimopsAction.COORDINATE.name()));
        vo.setWarnCount(countByFinalAction(tenantId, SimopsAction.WARN.name()));
        vo.setTopTypePairs(buildTopTypePairs(tenantId));
        vo.setTopAreas(buildTopAreas(tenantId));
        return vo;
    }

    @Override
    public void applyPreCheck(WorkPermitEntity permit, PermitCheckPoint checkPoint, PreCheckResultVO result) {
        if (checkPoint != PermitCheckPoint.SUBMIT && checkPoint != PermitCheckPoint.SITE_PERMIT) {
            return;
        }
        SimopsScanStage stage = SimopsScanStage.fromCheckPoint(checkPoint);
        SimopsScanResultVO scan = executeScan(permit, stage);
        appendPreCheckReasons(scan, result);
    }

    private SimopsScanResultVO executeScan(WorkPermitEntity permit, SimopsScanStage stage) {
        if (permit.getAreaId() == null) {
            return persistScan(permit, stage, Collections.<ConflictHit>emptyList());
        }
        if (permit.getPlanStartAt() == null || permit.getPlanEndAt() == null) {
            return persistScan(permit, stage, Collections.<ConflictHit>emptyList());
        }
        List<SimopsConflictRuleEntity> rules = loadActiveRules(permit.getTenantId());
        List<WorkPermitEntity> candidates = findOverlappingPermits(permit);
        List<ConflictHit> hits = new ArrayList<ConflictHit>();
        for (WorkPermitEntity other : candidates) {
            int overlapMinutes = overlapMinutes(permit, other);
            if (overlapMinutes <= 0) {
                continue;
            }
            SimopsConflictRuleEntity rule = matchRule(rules, permit, other, overlapMinutes);
            if (rule != null) {
                hits.add(new ConflictHit(other, rule, overlapMinutes));
            }
        }
        return persistScan(permit, stage, hits);
    }

    private SimopsScanResultVO persistScan(WorkPermitEntity permit, SimopsScanStage stage, List<ConflictHit> hits) {
        SimopsAction maxAction = null;
        for (ConflictHit hit : hits) {
            maxAction = SimopsAction.max(maxAction, SimopsAction.from(hit.rule.getAction()));
        }
        String finalAction = maxAction == null ? FINAL_PASS : maxAction.name();
        boolean passed = maxAction != SimopsAction.BLOCK;
        String suggestion = buildSuggestion(maxAction);

        SimopsScanResultEntity scan = new SimopsScanResultEntity();
        scan.setTenantId(permit.getTenantId());
        scan.setWorkPermitId(permit.getId());
        scan.setScanStage(stage.name());
        scan.setConflictCount(hits.size());
        scan.setMaxSeverity(maxAction == null ? null : maxAction.name());
        scan.setFinalAction(finalAction);
        scan.setPassed(passed ? 1 : 0);
        scan.setSuggestion(suggestion);
        scan.setScannedAt(new Date());
        scanResultMapper.insert(scan);

        List<SimopsConflictItemVO> itemVos = new ArrayList<SimopsConflictItemVO>();
        for (ConflictHit hit : hits) {
            SimopsConflictItemEntity item = buildConflictItem(scan, permit, hit);
            conflictItemMapper.insert(item);
            itemVos.add(toItemVO(item, hit.other.getPermitNo()));
        }

        SimopsScanResultVO vo = toScanVO(scan, false);
        vo.setItems(itemVos);
        vo.setReasons(buildReasons(itemVos));
        if (!passed) {
            log.info("simops-scan blocked permitId={} conflicts={}", permit.getId(), hits.size());
        }
        return vo;
    }

    private void appendPreCheckReasons(SimopsScanResultVO scan, PreCheckResultVO result) {
        if (scan.getReasons() == null || scan.getReasons().isEmpty()) {
            return;
        }
        boolean coordinationResolved = FINAL_PASS.equals(scan.getFinalAction())
                || hasApprovedCoordination(scan.getTenantId(), scan.getId());
        for (SimopsConflictReasonVO reason : scan.getReasons()) {
            String severity = reason.getSeverity();
            if (SimopsAction.BLOCK.name().equals(severity)) {
                result.getReasons().add(reason.getMessage());
            } else if (SimopsAction.COORDINATE.name().equals(severity)) {
                if (!coordinationResolved) {
                    result.getReasons().add("交叉作业需协调审批: " + reason.getMessage());
                }
            } else if (SimopsAction.WARN.name().equals(severity)) {
                result.getReasons().add(WARN_REASON_PREFIX + reason.getMessage());
            }
        }
    }

    private boolean hasApprovedCoordination(Long tenantId, Long scanResultId) {
        if (tenantId == null || scanResultId == null) {
            return false;
        }
        Long count = coordinationRecordMapper.selectCount(
                new LambdaQueryWrapper<SimopsCoordinationRecordEntity>()
                        .eq(SimopsCoordinationRecordEntity::getTenantId, tenantId)
                        .eq(SimopsCoordinationRecordEntity::getScanResultId, scanResultId)
                        .eq(SimopsCoordinationRecordEntity::getDecision, COORDINATION_APPROVED));
        return count != null && count > 0;
    }

    private List<WorkPermitEntity> findOverlappingPermits(WorkPermitEntity permit) {
        LambdaQueryWrapper<WorkPermitEntity> wrapper = new LambdaQueryWrapper<WorkPermitEntity>()
                .eq(WorkPermitEntity::getTenantId, permit.getTenantId())
                .eq(WorkPermitEntity::getDeleted, 0)
                .eq(WorkPermitEntity::getAreaId, permit.getAreaId())
                .ne(WorkPermitEntity::getId, permit.getId())
                .notIn(WorkPermitEntity::getStatus,
                        WorkPermitStatus.DRAFT.name(), WorkPermitStatus.CLOSED.name());
        List<WorkPermitEntity> all = workPermitMapper.selectList(wrapper);
        List<WorkPermitEntity> overlapping = new ArrayList<WorkPermitEntity>();
        for (WorkPermitEntity other : all) {
            if (timesOverlap(permit, other)) {
                overlapping.add(other);
            }
        }
        return overlapping;
    }

    private List<SimopsConflictRuleEntity> loadActiveRules(Long tenantId) {
        LambdaQueryWrapper<SimopsConflictRuleEntity> wrapper = ruleQuery(tenantId)
                .eq(SimopsConflictRuleEntity::getEnabled, 1);
        return ruleMapper.selectList(wrapper);
    }

    private SimopsConflictRuleEntity matchRule(List<SimopsConflictRuleEntity> rules, WorkPermitEntity current,
                                               WorkPermitEntity other, int overlapMinutes) {
        String typeA = normalizeWorkType(current.getWorkType());
        String typeB = normalizeWorkType(other.getWorkType());
        for (SimopsConflictRuleEntity rule : rules) {
            if (!areaMatches(rule, current.getAreaId())) {
                continue;
            }
            if (!typesMatch(rule, typeA, typeB)) {
                continue;
            }
            int required = rule.getOverlapMinutes() == null ? 0 : rule.getOverlapMinutes();
            if (overlapMinutes >= required) {
                return rule;
            }
        }
        return null;
    }

    private boolean typesMatch(SimopsConflictRuleEntity rule, String typeA, String typeB) {
        return rule.getWorkTypeA().equalsIgnoreCase(typeA) && rule.getWorkTypeB().equalsIgnoreCase(typeB)
                || rule.getWorkTypeA().equalsIgnoreCase(typeB) && rule.getWorkTypeB().equalsIgnoreCase(typeA);
    }

    private boolean areaMatches(SimopsConflictRuleEntity rule, Long areaId) {
        String scope = rule.getAreaScope() == null ? AREA_SCOPE_ALL : rule.getAreaScope().trim();
        if (AREA_SCOPE_ALL.equalsIgnoreCase(scope)) {
            return true;
        }
        return areaId != null && scope.equals(String.valueOf(areaId));
    }

    private boolean timesOverlap(WorkPermitEntity left, WorkPermitEntity right) {
        Date start1 = effectiveStart(left);
        Date end1 = effectiveEnd(left);
        Date start2 = effectiveStart(right);
        Date end2 = effectiveEnd(right);
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }
        return start1.before(end2) && start2.before(end1);
    }

    private int overlapMinutes(WorkPermitEntity left, WorkPermitEntity right) {
        Date start1 = effectiveStart(left);
        Date end1 = effectiveEnd(left);
        Date start2 = effectiveStart(right);
        Date end2 = effectiveEnd(right);
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return 0;
        }
        long overlapStart = Math.max(start1.getTime(), start2.getTime());
        long overlapEnd = Math.min(end1.getTime(), end2.getTime());
        if (overlapEnd <= overlapStart) {
            return 0;
        }
        return (int) ((overlapEnd - overlapStart) / 60000L);
    }

    private Date effectiveStart(WorkPermitEntity permit) {
        if (permit.getActualStartAt() != null) {
            return permit.getActualStartAt();
        }
        return permit.getPlanStartAt();
    }

    private Date effectiveEnd(WorkPermitEntity permit) {
        if (permit.getActualEndAt() != null) {
            return permit.getActualEndAt();
        }
        return permit.getPlanEndAt();
    }

    private SimopsConflictItemEntity buildConflictItem(SimopsScanResultEntity scan, WorkPermitEntity permit,
                                                       ConflictHit hit) {
        SimopsConflictItemEntity item = new SimopsConflictItemEntity();
        item.setTenantId(permit.getTenantId());
        item.setScanResultId(scan.getId());
        item.setWorkPermitId(permit.getId());
        item.setRelatedWorkPermitId(hit.other.getId());
        item.setRuleId(hit.rule.getId());
        item.setWorkTypeA(normalizeWorkType(permit.getWorkType()));
        item.setWorkTypeB(normalizeWorkType(hit.other.getWorkType()));
        item.setAction(hit.rule.getAction());
        item.setOverlapMinutes(hit.overlapMinutes);
        item.setMessage(buildConflictMessage(permit, hit.other, hit.rule));
        item.setCreatedAt(new Date());
        return item;
    }

    private String buildConflictMessage(WorkPermitEntity current, WorkPermitEntity other,
                                        SimopsConflictRuleEntity rule) {
        return "同区域存在" + describeWorkType(other.getWorkType()) + "作业 "
                + other.getPermitNo() + "，策略=" + rule.getAction();
    }

    private String describeWorkType(String workType) {
        if (!StringUtils.hasText(workType)) {
            return "未知类型";
        }
        return workType.trim();
    }

    private List<SimopsConflictReasonVO> buildReasons(List<SimopsConflictItemVO> items) {
        List<SimopsConflictReasonVO> reasons = new ArrayList<SimopsConflictReasonVO>();
        for (SimopsConflictItemVO item : items) {
            SimopsConflictReasonVO reason = new SimopsConflictReasonVO();
            reason.setCode(CODE_SIMOPS_CONFLICT);
            reason.setMessage(item.getMessage());
            reason.setSeverity(item.getAction());
            reason.setRelatedWorkPermitId(item.getRelatedWorkPermitId());
            reasons.add(reason);
        }
        return reasons;
    }

    private String buildSuggestion(SimopsAction maxAction) {
        if (maxAction == null) {
            return null;
        }
        if (maxAction == SimopsAction.BLOCK) {
            return "请调整作业时间或区域，消除硬阻断冲突后再提交";
        }
        if (maxAction == SimopsAction.COORDINATE) {
            return "请发起协调审批或调整作业计划";
        }
        return "请关注交叉作业风险并落实隔离防火措施";
    }

    private long countByFinalAction(Long tenantId, String action) {
        return scanResultMapper.selectCount(new LambdaQueryWrapper<SimopsScanResultEntity>()
                .eq(SimopsScanResultEntity::getTenantId, tenantId)
                .eq(SimopsScanResultEntity::getFinalAction, action));
    }

    private List<SimopsTypePairStatVO> buildTopTypePairs(Long tenantId) {
        List<SimopsConflictItemEntity> items = conflictItemMapper.selectList(
                new LambdaQueryWrapper<SimopsConflictItemEntity>()
                        .eq(SimopsConflictItemEntity::getTenantId, tenantId));
        Map<String, Long> counter = new HashMap<String, Long>();
        for (SimopsConflictItemEntity item : items) {
            String key = canonicalPair(item.getWorkTypeA(), item.getWorkTypeB());
            Long count = counter.get(key);
            counter.put(key, count == null ? 1L : count + 1L);
        }
        List<SimopsTypePairStatVO> stats = new ArrayList<SimopsTypePairStatVO>();
        for (Map.Entry<String, Long> entry : counter.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            SimopsTypePairStatVO stat = new SimopsTypePairStatVO();
            stat.setWorkTypeA(parts[0]);
            stat.setWorkTypeB(parts[1]);
            stat.setCount(entry.getValue());
            stats.add(stat);
        }
        Collections.sort(stats, new Comparator<SimopsTypePairStatVO>() {
            @Override
            public int compare(SimopsTypePairStatVO a, SimopsTypePairStatVO b) {
                return Long.compare(b.getCount() == null ? 0L : b.getCount(),
                        a.getCount() == null ? 0L : a.getCount());
            }
        });
        if (stats.size() > 10) {
            return new ArrayList<SimopsTypePairStatVO>(stats.subList(0, 10));
        }
        return stats;
    }

    private List<SimopsAreaStatVO> buildTopAreas(Long tenantId) {
        List<SimopsScanResultEntity> scans = scanResultMapper.selectList(
                new LambdaQueryWrapper<SimopsScanResultEntity>()
                        .eq(SimopsScanResultEntity::getTenantId, tenantId)
                        .gt(SimopsScanResultEntity::getConflictCount, 0));
        if (scans.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> permitIds = new HashSet<Long>();
        for (SimopsScanResultEntity scan : scans) {
            permitIds.add(scan.getWorkPermitId());
        }
        Map<Long, Long> areaCounter = new HashMap<Long, Long>();
        for (Long permitId : permitIds) {
            WorkPermitEntity permit = workPermitMapper.selectById(permitId);
            if (permit == null || permit.getAreaId() == null) {
                continue;
            }
            Long count = areaCounter.get(permit.getAreaId());
            areaCounter.put(permit.getAreaId(), count == null ? 1L : count + 1L);
        }
        List<SimopsAreaStatVO> stats = new ArrayList<SimopsAreaStatVO>();
        for (Map.Entry<Long, Long> entry : areaCounter.entrySet()) {
            SimopsAreaStatVO stat = new SimopsAreaStatVO();
            stat.setAreaId(entry.getKey());
            stat.setCount(entry.getValue());
            stats.add(stat);
        }
        Collections.sort(stats, new Comparator<SimopsAreaStatVO>() {
            @Override
            public int compare(SimopsAreaStatVO a, SimopsAreaStatVO b) {
                return Long.compare(b.getCount() == null ? 0L : b.getCount(),
                        a.getCount() == null ? 0L : a.getCount());
            }
        });
        if (stats.size() > 10) {
            return new ArrayList<SimopsAreaStatVO>(stats.subList(0, 10));
        }
        return stats;
    }

    private SimopsScanResultVO toScanVO(SimopsScanResultEntity entity, boolean loadItems) {
        SimopsScanResultVO vo = new SimopsScanResultVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setScanStage(entity.getScanStage());
        vo.setConflictCount(entity.getConflictCount());
        vo.setMaxSeverity(entity.getMaxSeverity());
        vo.setFinalAction(entity.getFinalAction());
        vo.setPassed(entity.getPassed() != null && entity.getPassed() == 1);
        vo.setSuggestion(entity.getSuggestion());
        vo.setScannedAt(entity.getScannedAt());
        if (loadItems) {
            List<SimopsConflictItemEntity> items = conflictItemMapper.selectList(
                    new LambdaQueryWrapper<SimopsConflictItemEntity>()
                            .eq(SimopsConflictItemEntity::getScanResultId, entity.getId())
                            .orderByDesc(SimopsConflictItemEntity::getCreatedAt));
            List<SimopsConflictItemVO> itemVos = new ArrayList<SimopsConflictItemVO>();
            for (SimopsConflictItemEntity item : items) {
                String permitNo = resolvePermitNo(item.getRelatedWorkPermitId());
                itemVos.add(toItemVO(item, permitNo));
            }
            vo.setItems(itemVos);
            vo.setReasons(buildReasons(itemVos));
        }
        return vo;
    }

    private String resolvePermitNo(Long permitId) {
        WorkPermitEntity entity = workPermitMapper.selectById(permitId);
        return entity == null ? null : entity.getPermitNo();
    }

    private SimopsConflictItemVO toItemVO(SimopsConflictItemEntity entity, String relatedPermitNo) {
        SimopsConflictItemVO vo = new SimopsConflictItemVO();
        vo.setId(entity.getId());
        vo.setScanResultId(entity.getScanResultId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setRelatedWorkPermitId(entity.getRelatedWorkPermitId());
        vo.setRelatedPermitNo(relatedPermitNo);
        vo.setRuleId(entity.getRuleId());
        vo.setWorkTypeA(entity.getWorkTypeA());
        vo.setWorkTypeB(entity.getWorkTypeB());
        vo.setAction(entity.getAction());
        vo.setOverlapMinutes(entity.getOverlapMinutes());
        vo.setMessage(entity.getMessage());
        return vo;
    }

    private SimopsConflictRuleVO toRuleVO(SimopsConflictRuleEntity entity) {
        SimopsConflictRuleVO vo = new SimopsConflictRuleVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setWorkTypeA(entity.getWorkTypeA());
        vo.setWorkTypeB(entity.getWorkTypeB());
        vo.setAreaScope(entity.getAreaScope());
        vo.setOverlapMinutes(entity.getOverlapMinutes());
        vo.setAction(entity.getAction());
        vo.setEnabled(entity.getEnabled() != null && entity.getEnabled() == 1);
        vo.setRemark(entity.getRemark());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private SimopsCoordinationRecordVO toCoordinationVO(SimopsCoordinationRecordEntity entity) {
        SimopsCoordinationRecordVO vo = new SimopsCoordinationRecordVO();
        vo.setId(entity.getId());
        vo.setScanResultId(entity.getScanResultId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setDecision(entity.getDecision());
        vo.setOpinion(entity.getOpinion());
        vo.setConditionsText(entity.getConditionsText());
        vo.setCoordinatorName(entity.getCoordinatorName());
        vo.setCoordinatedAt(entity.getCoordinatedAt());
        return vo;
    }

    private void applyRuleRequest(SimopsConflictRuleEntity entity, SimopsConflictRuleRequest request) {
        requireTenantId(request.getTenantId());
        entity.setTenantId(request.getTenantId());
        entity.setWorkTypeA(normalizeWorkType(request.getWorkTypeA()));
        entity.setWorkTypeB(normalizeWorkType(request.getWorkTypeB()));
        entity.setAreaScope(StringUtils.hasText(request.getAreaScope())
                ? request.getAreaScope().trim().toUpperCase() : AREA_SCOPE_ALL);
        entity.setOverlapMinutes(request.getOverlapMinutes() == null ? 0 : request.getOverlapMinutes());
        entity.setAction(SimopsAction.from(request.getAction()).name());
        entity.setEnabled(Boolean.FALSE.equals(request.getEnabled()) ? 0 : 1);
        entity.setRemark(normalizeText(request.getRemark()));
    }

    private LambdaQueryWrapper<SimopsConflictRuleEntity> ruleQuery(Long tenantId) {
        return new LambdaQueryWrapper<SimopsConflictRuleEntity>()
                .eq(SimopsConflictRuleEntity::getDeleted, 0)
                .and(w -> w.eq(SimopsConflictRuleEntity::getTenantId, tenantId)
                        .or().eq(SimopsConflictRuleEntity::getTenantId, 0L));
    }

    private SimopsConflictRuleEntity requireRule(Long tenantId, Long id) {
        requireTenantId(tenantId);
        SimopsConflictRuleEntity entity = ruleMapper.selectById(id);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() != 0) {
            throw new BusinessException(404, "simops rule not found");
        }
        if (!tenantId.equals(entity.getTenantId()) && entity.getTenantId() != 0L) {
            throw new BusinessException(404, "simops rule not found");
        }
        return entity;
    }

    private SimopsScanResultEntity requireScanResult(Long tenantId, Long id) {
        requireTenantId(tenantId);
        SimopsScanResultEntity entity = scanResultMapper.selectById(id);
        if (entity == null || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "simops scan result not found");
        }
        return entity;
    }

    private WorkPermitEntity requirePermit(Long tenantId, Long id) {
        requireTenantId(tenantId);
        WorkPermitEntity entity = workPermitMapper.selectById(id);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() != 0
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "work permit not found");
        }
        return entity;
    }

    private String canonicalPair(String a, String b) {
        String left = a == null ? "" : a;
        String right = b == null ? "" : b;
        if (left.compareTo(right) > 0) {
            String tmp = left;
            left = right;
            right = tmp;
        }
        return left + "|" + right;
    }

    private String normalizeWorkType(String workType) {
        return normalizeRequired(workType, "workType").toUpperCase();
    }

    private void requireTenantId(Long tenantId) {
        if (tenantId == null) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private String normalizeOperator(String operator) {
        return StringUtils.hasText(operator) ? operator.trim() : "system";
    }

    private String normalizeText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String normalizeRequired(String value, String field) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            throw new BusinessException(400, field + " is required");
        }
        return normalized;
    }

    private PageSpec normalizePage(int pageNo, int pageSize) {
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 100);
        return new PageSpec(normalizedPageNo, normalizedPageSize);
    }

    private static final class ConflictHit {
        private final WorkPermitEntity other;
        private final SimopsConflictRuleEntity rule;
        private final int overlapMinutes;

        private ConflictHit(WorkPermitEntity other, SimopsConflictRuleEntity rule, int overlapMinutes) {
            this.other = other;
            this.rule = rule;
            this.overlapMinutes = overlapMinutes;
        }
    }

    private static final class PageSpec {
        private final int pageNo;
        private final int pageSize;
        private final int offset;

        private PageSpec(int pageNo, int pageSize) {
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.offset = (pageNo - 1) * pageSize;
        }
    }
}
