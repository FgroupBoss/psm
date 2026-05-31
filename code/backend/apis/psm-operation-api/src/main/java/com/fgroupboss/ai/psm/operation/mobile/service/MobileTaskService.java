package com.fgroupboss.ai.psm.operation.mobile.service;

import com.fgroupboss.ai.psm.operation.mobile.client.AlarmClient;
import com.fgroupboss.ai.psm.operation.mobile.client.WorkPermitClient;
import com.fgroupboss.ai.psm.operation.client.dto.AlarmEventVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitVO;
import com.fgroupboss.ai.psm.operation.mobile.config.MobileRole;
import com.fgroupboss.ai.psm.operation.mobile.model.vo.MobileTaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 按角色聚合移动端待办（作业票 + 报警）。
 */
@Service
@RequiredArgsConstructor
public class MobileTaskService {

    private static final List<String> SITE_PERMIT_STATUSES = Collections.singletonList("PENDING_SITE_PERMIT");
    private static final List<String> MONITOR_STATUSES = Arrays.asList("IN_PROGRESS", "SUSPENDED");
    private static final List<String> ACCEPTANCE_STATUSES = Collections.singletonList("PENDING_ACCEPTANCE");

    private final WorkPermitClient workPermitClient;
    private final AlarmClient alarmClient;

    public List<MobileTaskVO> listTasks(Long tenantId, Long userId, MobileRole role) {
        if (tenantId == null) {
            return Collections.emptyList();
        }
        List<MobileTaskVO> tasks = new ArrayList<MobileTaskVO>();
        tasks.addAll(buildPermitTasks(tenantId, userId, role));
        tasks.addAll(buildAlarmTasks(tenantId));
        tasks.sort(Comparator.comparing(MobileTaskVO::getDueAt, Comparator.nullsLast(Comparator.naturalOrder())));
        return tasks;
    }

    private List<MobileTaskVO> buildPermitTasks(Long tenantId, Long userId, MobileRole role) {
        List<MobileTaskVO> tasks = new ArrayList<MobileTaskVO>();
        if (role == MobileRole.ALL || role == MobileRole.PERMIT_ISSUER || role == MobileRole.SUPERVISOR) {
            tasks.addAll(toPermitTasks(workPermitClient.page(tenantId, "PENDING_SITE_PERMIT", 1, 50),
                    "SITE_PERMIT", "待现场许可", userId, role,
                    SITE_PERMIT_STATUSES, MobileRole.PERMIT_ISSUER, MobileRole.SUPERVISOR));
        }
        if (role == MobileRole.ALL || role == MobileRole.GUARDIAN) {
            for (String status : MONITOR_STATUSES) {
                tasks.addAll(toPermitTasks(workPermitClient.page(tenantId, status, 1, 50),
                        "MONITOR", "待监护巡检", userId, role,
                        MONITOR_STATUSES, MobileRole.GUARDIAN));
            }
        }
        if (role == MobileRole.ALL || role == MobileRole.SUPERVISOR) {
            tasks.addAll(toPermitTasks(workPermitClient.page(tenantId, "PENDING_ACCEPTANCE", 1, 50),
                    "ACCEPTANCE", "待完工验收", userId, role,
                    ACCEPTANCE_STATUSES, MobileRole.SUPERVISOR));
        }
        return tasks;
    }

    private List<MobileTaskVO> toPermitTasks(List<WorkPermitVO> permits, String taskType, String actionHint,
                                             Long userId, MobileRole requestRole, List<String> allowedStatuses,
                                             MobileRole... ownerRoles) {
        List<MobileTaskVO> tasks = new ArrayList<MobileTaskVO>();
        for (WorkPermitVO permit : permits) {
            if (permit == null || permit.getId() == null) {
                continue;
            }
            if (!allowedStatuses.contains(permit.getStatus())) {
                continue;
            }
            if (requestRole != MobileRole.ALL && !matchesRole(permit, userId, requestRole, ownerRoles)) {
                continue;
            }
            MobileTaskVO task = new MobileTaskVO();
            task.setTaskType(taskType);
            task.setBizType("WORK_PERMIT");
            task.setBizId(permit.getId());
            task.setTitle(permit.getTitle() == null ? permit.getPermitNo() : permit.getTitle());
            task.setStatus(permit.getStatus());
            task.setPriority(resolvePriority(permit.getStatus()));
            task.setDueAt(permit.getPlanEndAt() == null ? permit.getPlanStartAt() : permit.getPlanEndAt());
            task.setActionHint(actionHint);
            tasks.add(task);
        }
        return tasks;
    }

    private boolean matchesRole(WorkPermitVO permit, Long userId, MobileRole requestRole, MobileRole... ownerRoles) {
        if (userId == null) {
            return true;
        }
        if (requestRole == MobileRole.ALL) {
            return isOwner(permit, userId, ownerRoles);
        }
        return isOwner(permit, userId, requestRole);
    }

    private boolean isOwner(WorkPermitVO permit, Long userId, MobileRole... roles) {
        for (MobileRole role : roles) {
            if (isOwner(permit, userId, role)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOwner(WorkPermitVO permit, Long userId, MobileRole role) {
        if (role == MobileRole.GUARDIAN) {
            return userId.equals(permit.getGuardianUserId());
        }
        if (role == MobileRole.PERMIT_ISSUER) {
            return userId.equals(permit.getPermitIssuerUserId());
        }
        if (role == MobileRole.SUPERVISOR) {
            return userId.equals(permit.getSupervisorUserId());
        }
        return true;
    }

    private String resolvePriority(String status) {
        if ("SUSPENDED".equals(status)) {
            return "HIGH";
        }
        if ("PENDING_SITE_PERMIT".equals(status)) {
            return "MEDIUM";
        }
        return "NORMAL";
    }

    private List<MobileTaskVO> buildAlarmTasks(Long tenantId) {
        List<MobileTaskVO> tasks = new ArrayList<MobileTaskVO>();
        for (AlarmEventVO alarm : alarmClient.listPending(tenantId, 20)) {
            if (alarm == null || alarm.getId() == null) {
                continue;
            }
            MobileTaskVO task = new MobileTaskVO();
            task.setTaskType("ALARM_FEEDBACK");
            task.setBizType("ALARM");
            task.setBizId(alarm.getId());
            task.setTitle(alarm.getTitle());
            task.setStatus(alarm.getStatus());
            task.setPriority(alarm.getAlarmLevel());
            task.setDueAt(alarm.getLastOccurredAt() == null ? new Date() : alarm.getLastOccurredAt());
            task.setActionHint("待报警处置反馈");
            tasks.add(task);
        }
        return tasks;
    }
}
