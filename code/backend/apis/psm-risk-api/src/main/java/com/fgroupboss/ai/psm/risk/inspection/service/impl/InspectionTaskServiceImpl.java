package com.fgroupboss.ai.psm.risk.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.risk.inspection.config.ItemResultStatus;
import com.fgroupboss.ai.psm.risk.inspection.config.TaskStatus;
import com.fgroupboss.ai.psm.risk.inspection.client.DualPreventionClient;
import com.fgroupboss.ai.psm.risk.inspection.client.dto.HazardCreatePayload;
import com.fgroupboss.ai.psm.risk.inspection.client.dto.HazardCreateResult;
import com.fgroupboss.ai.psm.risk.inspection.mapper.AbnormalRecordMapper;
import com.fgroupboss.ai.psm.risk.inspection.mapper.ChecklistItemMapper;
import com.fgroupboss.ai.psm.risk.inspection.mapper.InspectionRouteMapper;
import com.fgroupboss.ai.psm.risk.inspection.mapper.InspectionPlanMapper;
import com.fgroupboss.ai.psm.risk.inspection.mapper.InspectionTaskMapper;
import com.fgroupboss.ai.psm.risk.inspection.mapper.RoutePointMapper;
import com.fgroupboss.ai.psm.risk.inspection.mapper.SignRecordMapper;
import com.fgroupboss.ai.psm.risk.inspection.mapper.TaskItemMapper;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.OverdueScanRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.TaskAbnormalRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.TaskCreateRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.TaskItemResultRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.TaskItemSubmitRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.TaskSignInRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.TaskStartRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.AbnormalRecordEntity;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.ChecklistItemEntity;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.InspectionPlanEntity;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.InspectionRouteEntity;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.InspectionTaskEntity;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.RoutePointEntity;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.SignRecordEntity;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.TaskItemEntity;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.AbnormalRecordVO;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.InspectionStatisticsVO;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.InspectionTaskVO;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.OverdueScanResultVO;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.SignRecordVO;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.TaskItemVO;
import com.fgroupboss.ai.psm.risk.inspection.service.InspectionTaskService;
import com.fgroupboss.ai.psm.risk.inspection.support.InspectionAuditSupport;
import com.fgroupboss.ai.psm.risk.inspection.support.InspectionSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 巡检任务执行与统计业务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionTaskServiceImpl implements InspectionTaskService {

    private final InspectionTaskMapper taskMapper;
    private final InspectionPlanMapper planMapper;
    private final InspectionRouteMapper routeMapper;
    private final RoutePointMapper routePointMapper;
    private final TaskItemMapper taskItemMapper;
    private final ChecklistItemMapper checklistItemMapper;
    private final SignRecordMapper signRecordMapper;
    private final AbnormalRecordMapper abnormalRecordMapper;
    private final DualPreventionClient dualPreventionClient;
    private final InspectionAuditSupport auditSupport;

    @Override
    public PageResult<InspectionTaskVO> page(Long tenantId, String status, Long executorId,
                                             int pageNo, int pageSize) {
        InspectionSupport.requireTenantId(tenantId);
        InspectionSupport.Page page = InspectionSupport.normalizePage(pageNo, pageSize);
        String normalizedStatus = InspectionSupport.normalizeText(status);
        long total = taskMapper.countByTenant(tenantId, normalizedStatus, executorId);
        List<InspectionTaskEntity> entities = total == 0
                ? new ArrayList<InspectionTaskEntity>()
                : taskMapper.listByTenant(tenantId, normalizedStatus, executorId, page.offset, page.pageSize);
        List<InspectionTaskVO> records = new ArrayList<InspectionTaskVO>();
        for (InspectionTaskEntity entity : entities) {
            records.add(toVO(entity, false));
        }
        return new PageResult<InspectionTaskVO>(total, page.pageNo, page.pageSize, records);
    }

    @Override
    public InspectionTaskVO getById(Long tenantId, Long id) {
        return toVO(requireTask(tenantId, id), true);
    }

    @Override
    @Transactional
    public InspectionTaskVO create(TaskCreateRequest request) {
        InspectionSupport.requireTenantId(request.getTenantId());
        requireRoute(request.getTenantId(), request.getRouteId());
        if (request.getScheduledEnd().isBefore(request.getScheduledStart())) {
            throw new BusinessException(400, "scheduledEnd must be after scheduledStart");
        }
        InspectionTaskEntity entity = new InspectionTaskEntity();
        entity.setTenantId(request.getTenantId());
        entity.setTaskNo(InspectionSupport.nextTaskNo());
        entity.setPlanId(request.getPlanId());
        entity.setRouteId(request.getRouteId());
        entity.setScheduledStart(request.getScheduledStart());
        entity.setScheduledEnd(request.getScheduledEnd());
        entity.setExecutorId(request.getExecutorId());
        entity.setStatus(TaskStatus.PENDING.name());
        entity.setCompletionRate(BigDecimal.ZERO);
        entity.setAbnormalCount(0);
        entity.setDeleted(0);
        taskMapper.insert(entity);
        return toVO(entity, false);
    }

    @Override
    @Transactional
    public InspectionTaskVO start(Long taskId, TaskStartRequest request) {
        InspectionTaskEntity entity = requireTask(request.getTenantId(), taskId);
        TaskStatus.assertStart(entity.getStatus());
        entity.setStatus(TaskStatus.IN_PROGRESS.name());
        entity.setActualStart(LocalDateTime.now());
        if (request.getExecutorId() != null) {
            entity.setExecutorId(request.getExecutorId());
        }
        taskMapper.updateById(entity);
        return toVO(entity, true);
    }

    @Override
    @Transactional
    public SignRecordVO signIn(Long taskId, TaskSignInRequest request) {
        InspectionTaskEntity task = requireTask(request.getTenantId(), taskId);
        TaskStatus.assertInProgress(task.getStatus());
        RoutePointEntity point = routePointMapper.findByIdAndRoute(
                request.getTenantId(), task.getRouteId(), request.getRoutePointId());
        if (point == null) {
            throw new BusinessException(404, "route point not found on task route");
        }
        SignRecordEntity existing = signRecordMapper.findByTaskAndPoint(
                request.getTenantId(), taskId, request.getRoutePointId());
        if (existing != null) {
            throw new BusinessException(400, "route point already signed in");
        }
        SignRecordEntity record = new SignRecordEntity();
        record.setTenantId(request.getTenantId());
        record.setTaskId(taskId);
        record.setRoutePointId(request.getRoutePointId());
        record.setSignType(request.getSignType().trim());
        record.setSignCode(request.getSignCode());
        record.setSignedAt(LocalDateTime.now());
        record.setOperatorId(request.getOperatorId());
        record.setOperatorName(request.getOperatorName());
        signRecordMapper.insert(record);
        return toSignVO(record);
    }

    @Override
    @Transactional
    public InspectionTaskVO submitItems(Long taskId, TaskItemSubmitRequest request) {
        InspectionTaskEntity task = requireTask(request.getTenantId(), taskId);
        TaskStatus.assertInProgress(task.getStatus());
        int abnormalDelta = 0;
        for (TaskItemResultRequest itemRequest : request.getItems()) {
            ItemResultStatus resultStatus = ItemResultStatus.fromCode(itemRequest.getResultStatus());
            validateItemResult(itemRequest, resultStatus);
            requireChecklistItem(request.getTenantId(), itemRequest.getChecklistItemId());
            TaskItemEntity existing = taskItemMapper.findByTaskAndChecklistItem(
                    request.getTenantId(), taskId, itemRequest.getChecklistItemId());
            boolean wasAbnormal = existing != null && ItemResultStatus.ABNORMAL.name().equals(existing.getResultStatus());
            TaskItemEntity entity = existing != null ? existing : newTaskItem(request.getTenantId(), taskId, itemRequest);
            entity.setRoutePointId(itemRequest.getRoutePointId());
            entity.setResultValue(itemRequest.getResultValue());
            entity.setResultStatus(resultStatus.name());
            entity.setPhotoUrls(itemRequest.getPhotoUrls());
            entity.setRemark(itemRequest.getRemark());
            entity.setCheckedAt(LocalDateTime.now());
            if (existing == null) {
                taskItemMapper.insert(entity);
            } else {
                taskItemMapper.updateById(entity);
            }
            if (resultStatus == ItemResultStatus.ABNORMAL && !wasAbnormal) {
                abnormalDelta++;
            } else if (resultStatus != ItemResultStatus.ABNORMAL && wasAbnormal) {
                abnormalDelta--;
            }
        }
        refreshCompletion(task);
        if (abnormalDelta != 0) {
            int count = task.getAbnormalCount() == null ? 0 : task.getAbnormalCount();
            task.setAbnormalCount(Math.max(0, count + abnormalDelta));
            taskMapper.updateById(task);
        }
        return toVO(requireTask(request.getTenantId(), taskId), true);
    }

    @Override
    @Transactional
    public InspectionTaskVO complete(Long tenantId, Long taskId) {
        InspectionTaskEntity task = requireTask(tenantId, taskId);
        String before = task.getStatus();
        TaskStatus.assertComplete(task.getStatus());
        refreshCompletion(task);
        task.setStatus(TaskStatus.COMPLETED.name());
        task.setActualEnd(LocalDateTime.now());
        taskMapper.updateById(task);
        auditSupport.append(tenantId, taskId, "COMPLETE", before, task.getStatus(), "system");
        return toVO(task, true);
    }

    @Override
    @Transactional
    public AbnormalRecordVO registerAbnormal(Long taskId, TaskAbnormalRequest request) {
        InspectionTaskEntity task = requireTask(request.getTenantId(), taskId);
        TaskStatus.assertInProgress(task.getStatus());
        if (!StringUtils.hasText(request.getPhotoUrls())) {
            throw new BusinessException(400, "photoUrls is required for abnormal record");
        }
        AbnormalRecordEntity record = new AbnormalRecordEntity();
        record.setTenantId(request.getTenantId());
        record.setTaskId(taskId);
        record.setTaskItemId(request.getTaskItemId());
        record.setRoutePointId(request.getRoutePointId());
        record.setAbnormalDesc(request.getAbnormalDesc().trim());
        record.setPhotoUrls(request.getPhotoUrls().trim());
        record.setSeverity(request.getSeverity());
        record.setHandleStatus("OPEN");
        abnormalRecordMapper.insert(record);
        int count = task.getAbnormalCount() == null ? 0 : task.getAbnormalCount();
        task.setAbnormalCount(count + 1);
        taskMapper.updateById(task);
        linkHazardIfNeeded(request, record);
        auditSupport.append(request.getTenantId(), taskId, "ABNORMAL_REGISTER", null,
                record.getId() != null ? String.valueOf(record.getId()) : null, "system");
        return toAbnormalVO(record);
    }

    private void linkHazardIfNeeded(TaskAbnormalRequest request, AbnormalRecordEntity record) {
        if (Boolean.FALSE.equals(request.getCreateHazard())) {
            return;
        }
        try {
            HazardCreatePayload payload = new HazardCreatePayload();
            payload.setTenantId(request.getTenantId());
            payload.setHazardLevel(StringUtils.hasText(request.getSeverity()) ? request.getSeverity().trim() : "GENERAL");
            payload.setSourceType("INSPECTION");
            payload.setSourceBizId(record.getId());
            payload.setDescription(request.getAbnormalDesc().trim());
            payload.setFoundAt(LocalDateTime.now());
            HazardCreateResult hazard = dualPreventionClient.createHazard(payload);
            if (hazard != null && hazard.getId() != null) {
                record.setHazardDraftId(hazard.getId());
                abnormalRecordMapper.updateById(record);
            }
        } catch (Exception ex) {
            log.warn("dual-prevention hazard create failed abnormalId={} reason={}", record.getId(), ex.getMessage());
        }
    }

    @Override
    @Transactional
    public OverdueScanResultVO overdueScan(OverdueScanRequest request) {
        InspectionSupport.requireTenantId(request.getTenantId());
        LocalDateTime now = LocalDateTime.now();
        List<InspectionTaskEntity> overdueTasks = taskMapper.listOverdue(request.getTenantId(), now);
        int missed = 0;
        for (InspectionTaskEntity task : overdueTasks) {
            int updated = taskMapper.markMissed(request.getTenantId(), task.getId(), now);
            if (updated > 0) {
                missed++;
            }
        }
        OverdueScanResultVO result = new OverdueScanResultVO();
        result.setScannedCount(overdueTasks.size());
        result.setMissedCount(missed);
        return result;
    }

    @Override
    public InspectionStatisticsVO statistics(Long tenantId, LocalDateTime from, LocalDateTime to) {
        InspectionSupport.requireTenantId(tenantId);
        Map<String, Object> row = taskMapper.aggregateStatistics(tenantId, from, to);
        long total = toLong(row.get("total_count"));
        long completed = toLong(row.get("completed_count"));
        long missed = toLong(row.get("missed_count"));
        long abnormalItems = toLong(row.get("abnormal_item_count"));
        InspectionStatisticsVO vo = new InspectionStatisticsVO();
        vo.setTotalCount(total);
        vo.setPendingCount(toLong(row.get("pending_count")));
        vo.setInProgressCount(toLong(row.get("in_progress_count")));
        vo.setCompletedCount(completed);
        vo.setMissedCount(missed);
        vo.setAbnormalItemCount(abnormalItems);
        vo.setCompletionRate(rate(completed, total));
        vo.setMissedRate(rate(missed, total));
        vo.setAbnormalRate(rate(abnormalItems, total));
        return vo;
    }

    @Override
    public List<InspectionTaskVO> listRecentByMajorHazard(Long tenantId, Long majorHazardId, int limit) {
        InspectionSupport.requireTenantId(tenantId);
        if (majorHazardId == null || majorHazardId <= 0) {
            throw new BusinessException(400, "majorHazardId is required");
        }
        int normalizedLimit = limit <= 0 ? 10 : Math.min(limit, 50);
        LambdaQueryWrapper<InspectionPlanEntity> planWrapper = new LambdaQueryWrapper<InspectionPlanEntity>();
        planWrapper.eq(InspectionPlanEntity::getTenantId, tenantId)
                .eq(InspectionPlanEntity::getMajorHazardId, majorHazardId)
                .eq(InspectionPlanEntity::getDeleted, 0);
        List<InspectionPlanEntity> plans = planMapper.selectList(planWrapper);
        if (plans.isEmpty()) {
            return new ArrayList<InspectionTaskVO>();
        }
        List<Long> planIds = new ArrayList<Long>();
        for (InspectionPlanEntity plan : plans) {
            planIds.add(plan.getId());
        }
        LambdaQueryWrapper<InspectionTaskEntity> taskWrapper = new LambdaQueryWrapper<InspectionTaskEntity>();
        taskWrapper.eq(InspectionTaskEntity::getTenantId, tenantId)
                .in(InspectionTaskEntity::getPlanId, planIds)
                .eq(InspectionTaskEntity::getDeleted, 0)
                .orderByDesc(InspectionTaskEntity::getScheduledStart)
                .last("LIMIT " + normalizedLimit);
        List<InspectionTaskEntity> tasks = taskMapper.selectList(taskWrapper);
        List<InspectionTaskVO> records = new ArrayList<InspectionTaskVO>();
        for (InspectionTaskEntity task : tasks) {
            records.add(toVO(task, false));
        }
        return records;
    }

    private void validateItemResult(TaskItemResultRequest itemRequest, ItemResultStatus resultStatus) {
        if (resultStatus == ItemResultStatus.ABNORMAL && !StringUtils.hasText(itemRequest.getPhotoUrls())) {
            throw new BusinessException(400, "photoUrls is required when result is ABNORMAL");
        }
    }

    private TaskItemEntity newTaskItem(Long tenantId, Long taskId, TaskItemResultRequest itemRequest) {
        TaskItemEntity entity = new TaskItemEntity();
        entity.setTenantId(tenantId);
        entity.setTaskId(taskId);
        entity.setChecklistItemId(itemRequest.getChecklistItemId());
        return entity;
    }

    private void refreshCompletion(InspectionTaskEntity task) {
        long checked = taskItemMapper.countChecked(task.getTenantId(), task.getId());
        long expected = countExpectedItems(task);
        BigDecimal rate = expected == 0
                ? BigDecimal.valueOf(checked > 0 ? 100 : 0)
                : BigDecimal.valueOf(checked * 100.0 / expected).setScale(2, RoundingMode.HALF_UP);
        task.setCompletionRate(rate);
        taskMapper.updateById(task);
    }

    private long countExpectedItems(InspectionTaskEntity task) {
        List<RoutePointEntity> points = routePointMapper.listByRoute(task.getTenantId(), task.getRouteId());
        long total = 0;
        for (RoutePointEntity point : points) {
            if (point.getChecklistTemplateId() == null) {
                continue;
            }
            total += checklistItemMapper.listByTemplate(task.getTenantId(), point.getChecklistTemplateId()).size();
        }
        if (total == 0) {
            total = taskItemMapper.countChecked(task.getTenantId(), task.getId());
            if (total == 0) {
                total = 1;
            }
        }
        return total;
    }

    private void requireRoute(Long tenantId, Long routeId) {
        InspectionRouteEntity route = routeMapper.selectById(routeId);
        if (route == null || (route.getDeleted() != null && route.getDeleted() == 1)
                || !tenantId.equals(route.getTenantId())) {
            throw new BusinessException(404, "inspection route not found");
        }
    }

    private void requireChecklistItem(Long tenantId, Long checklistItemId) {
        ChecklistItemEntity item = checklistItemMapper.selectById(checklistItemId);
        if (item == null || (item.getDeleted() != null && item.getDeleted() == 1)
                || !tenantId.equals(item.getTenantId())) {
            throw new BusinessException(404, "checklist item not found");
        }
    }

    private InspectionTaskEntity requireTask(Long tenantId, Long id) {
        InspectionSupport.requireTenantId(tenantId);
        InspectionTaskEntity entity = taskMapper.selectById(id);
        if (entity == null || (entity.getDeleted() != null && entity.getDeleted() == 1)
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "inspection task not found");
        }
        return entity;
    }

    private InspectionTaskVO toVO(InspectionTaskEntity entity, boolean withItems) {
        InspectionTaskVO vo = new InspectionTaskVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setTaskNo(entity.getTaskNo());
        vo.setPlanId(entity.getPlanId());
        vo.setRouteId(entity.getRouteId());
        vo.setScheduledStart(entity.getScheduledStart());
        vo.setScheduledEnd(entity.getScheduledEnd());
        vo.setActualStart(entity.getActualStart());
        vo.setActualEnd(entity.getActualEnd());
        vo.setExecutorId(entity.getExecutorId());
        vo.setStatus(entity.getStatus());
        vo.setCompletionRate(entity.getCompletionRate());
        vo.setAbnormalCount(entity.getAbnormalCount());
        if (withItems) {
            List<TaskItemVO> items = new ArrayList<TaskItemVO>();
            for (TaskItemEntity item : taskItemMapper.listByTask(entity.getTenantId(), entity.getId())) {
                items.add(toItemVO(item));
            }
            vo.setItems(items);
        }
        return vo;
    }

    private TaskItemVO toItemVO(TaskItemEntity entity) {
        TaskItemVO vo = new TaskItemVO();
        vo.setId(entity.getId());
        vo.setChecklistItemId(entity.getChecklistItemId());
        vo.setRoutePointId(entity.getRoutePointId());
        vo.setResultValue(entity.getResultValue());
        vo.setResultStatus(entity.getResultStatus());
        vo.setPhotoUrls(entity.getPhotoUrls());
        vo.setRemark(entity.getRemark());
        vo.setCheckedAt(entity.getCheckedAt());
        return vo;
    }

    private SignRecordVO toSignVO(SignRecordEntity entity) {
        SignRecordVO vo = new SignRecordVO();
        vo.setId(entity.getId());
        vo.setTaskId(entity.getTaskId());
        vo.setRoutePointId(entity.getRoutePointId());
        vo.setSignType(entity.getSignType());
        vo.setSignCode(entity.getSignCode());
        vo.setSignedAt(entity.getSignedAt());
        vo.setOperatorId(entity.getOperatorId());
        vo.setOperatorName(entity.getOperatorName());
        return vo;
    }

    private AbnormalRecordVO toAbnormalVO(AbnormalRecordEntity entity) {
        AbnormalRecordVO vo = new AbnormalRecordVO();
        vo.setId(entity.getId());
        vo.setTaskId(entity.getTaskId());
        vo.setTaskItemId(entity.getTaskItemId());
        vo.setRoutePointId(entity.getRoutePointId());
        vo.setAbnormalDesc(entity.getAbnormalDesc());
        vo.setPhotoUrls(entity.getPhotoUrls());
        vo.setSeverity(entity.getSeverity());
        vo.setHandleStatus(entity.getHandleStatus());
        vo.setHazardDraftId(entity.getHazardDraftId());
        return vo;
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private BigDecimal rate(long part, long total) {
        if (total == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(part * 100.0 / total).setScale(2, RoundingMode.HALF_UP);
    }
}
