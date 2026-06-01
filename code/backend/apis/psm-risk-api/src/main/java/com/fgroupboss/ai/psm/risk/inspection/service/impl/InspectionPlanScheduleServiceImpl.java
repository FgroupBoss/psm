package com.fgroupboss.ai.psm.risk.inspection.service.impl;

import com.fgroupboss.ai.psm.risk.inspection.config.TaskStatus;
import com.fgroupboss.ai.psm.risk.inspection.mapper.InspectionPlanMapper;
import com.fgroupboss.ai.psm.risk.inspection.mapper.InspectionRouteMapper;
import com.fgroupboss.ai.psm.risk.inspection.mapper.InspectionTaskMapper;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.InspectionPlanEntity;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.InspectionRouteEntity;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.InspectionTaskEntity;
import com.fgroupboss.ai.psm.risk.inspection.service.InspectionPlanScheduleService;
import com.fgroupboss.ai.psm.risk.inspection.support.InspectionSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 按 DAILY/WEEKLY 周期为启用计划生成待执行任务。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionPlanScheduleServiceImpl implements InspectionPlanScheduleService {

    private static final LocalTime DEFAULT_START = LocalTime.of(8, 0);
    private static final LocalTime DEFAULT_END = LocalTime.of(18, 0);

    private final InspectionPlanMapper planMapper;
    private final InspectionTaskMapper taskMapper;
    private final InspectionRouteMapper routeMapper;

    @Override
    @Transactional
    public int generateDueTasks() {
        List<InspectionPlanEntity> plans = planMapper.listEnabled();
        if (plans == null || plans.isEmpty()) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        int created = 0;
        for (InspectionPlanEntity plan : plans) {
            ScheduleWindow window = resolveWindow(plan.getCycleType(), now.toLocalDate());
            if (window == null) {
                continue;
            }
            long pending = taskMapper.countPendingInWindow(
                    plan.getTenantId(), plan.getId(), window.getStart(), window.getEnd());
            if (pending > 0) {
                continue;
            }
            InspectionRouteEntity route = routeMapper.selectById(plan.getRouteId());
            if (route == null || route.getDeleted() != null && route.getDeleted() == 1) {
                log.warn("skip plan task generation planId={} route missing", plan.getId());
                continue;
            }
            LocalDateTime scheduledStart = window.getTaskStart();
            LocalDateTime scheduledEnd = resolveTaskEnd(scheduledStart, route.getEstimatedMinutes());
            InspectionTaskEntity task = new InspectionTaskEntity();
            task.setTenantId(plan.getTenantId());
            task.setTaskNo(InspectionSupport.nextTaskNo());
            task.setPlanId(plan.getId());
            task.setRouteId(plan.getRouteId());
            task.setScheduledStart(scheduledStart);
            task.setScheduledEnd(scheduledEnd);
            task.setExecutorId(plan.getDefaultExecutorId());
            task.setStatus(TaskStatus.PENDING.name());
            task.setCompletionRate(BigDecimal.ZERO);
            task.setAbnormalCount(0);
            task.setDeleted(0);
            taskMapper.insert(task);
            created++;
        }
        if (created > 0) {
            log.info("inspection plan scheduler created {} task(s)", created);
        }
        return created;
    }

    private ScheduleWindow resolveWindow(String cycleType, LocalDate today) {
        if ("DAILY".equalsIgnoreCase(cycleType)) {
            LocalDateTime start = today.atStartOfDay();
            LocalDateTime end = today.atTime(23, 59, 59);
            LocalDateTime taskStart = today.atTime(DEFAULT_START);
            return new ScheduleWindow(start, end, taskStart);
        }
        if ("WEEKLY".equalsIgnoreCase(cycleType)) {
            LocalDate monday = today.with(DayOfWeek.MONDAY);
            LocalDate sunday = today.with(DayOfWeek.SUNDAY);
            LocalDateTime start = monday.atStartOfDay();
            LocalDateTime end = sunday.atTime(23, 59, 59);
            LocalDateTime taskStart = monday.atTime(DEFAULT_START);
            return new ScheduleWindow(start, end, taskStart);
        }
        return null;
    }

    private LocalDateTime resolveTaskEnd(LocalDateTime scheduledStart, Integer estimatedMinutes) {
        if (estimatedMinutes != null && estimatedMinutes > 0) {
            return scheduledStart.plusMinutes(estimatedMinutes);
        }
        LocalDateTime defaultEnd = scheduledStart.toLocalDate().atTime(DEFAULT_END);
        if (defaultEnd.isAfter(scheduledStart)) {
            return defaultEnd;
        }
        return scheduledStart.plusHours(8);
    }

    private static final class ScheduleWindow {
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final LocalDateTime taskStart;

        private ScheduleWindow(LocalDateTime start, LocalDateTime end, LocalDateTime taskStart) {
            this.start = start;
            this.end = end;
            this.taskStart = taskStart;
        }

        private LocalDateTime getStart() {
            return start;
        }

        private LocalDateTime getEnd() {
            return end;
        }

        private LocalDateTime getTaskStart() {
            return taskStart;
        }
    }
}
