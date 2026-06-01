package com.fgroupboss.ai.psm.operation.workpermit.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.notification.CentralNotificationClient;
import com.fgroupboss.ai.psm.common.notification.NotificationSendRequest;
import com.fgroupboss.ai.psm.operation.workpermit.config.WorkPermitStatus;
import com.fgroupboss.ai.psm.operation.workpermit.mapper.WorkPermitMapper;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 扫描已超过计划结束时间且仍在作业中的作业票，发送超时提醒。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkPermitTimeoutNotificationScheduler {

    private final WorkPermitMapper workPermitMapper;
    private final CentralNotificationClient notificationClient;

    @Scheduled(cron = "${psm.work-permit-timeout-cron:0 */15 * * * ?}")
    public void scanTimeoutPermits() {
        Date now = new Date();
        LambdaQueryWrapper<WorkPermitEntity> wrapper = new LambdaQueryWrapper<WorkPermitEntity>()
                .eq(WorkPermitEntity::getDeleted, 0)
                .eq(WorkPermitEntity::getStatus, WorkPermitStatus.IN_PROGRESS.name())
                .isNotNull(WorkPermitEntity::getPlanEndAt)
                .lt(WorkPermitEntity::getPlanEndAt, now);
        for (WorkPermitEntity permit : workPermitMapper.selectList(wrapper)) {
            notifyRecipients(permit, now);
        }
    }

    private void notifyRecipients(WorkPermitEntity permit, Date now) {
        Set<Long> userIds = new HashSet<Long>();
        if (permit.getSupervisorUserId() != null) {
            userIds.add(permit.getSupervisorUserId());
        }
        if (permit.getGuardianUserId() != null) {
            userIds.add(permit.getGuardianUserId());
        }
        if (permit.getPermitIssuerUserId() != null) {
            userIds.add(permit.getPermitIssuerUserId());
        }
        if (userIds.isEmpty()) {
            return;
        }
        SimpleDateFormat dayFmt = new SimpleDateFormat("yyyyMMdd");
        String day = dayFmt.format(now);
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("permitNo", permit.getPermitNo() == null ? String.valueOf(permit.getId()) : permit.getPermitNo());
        variables.put("status", permit.getStatus());
        for (Long userId : userIds) {
            String requestId = "WKP-TIMEOUT-" + permit.getId() + "-" + userId + "-" + day;
            NotificationSendRequest request = CentralNotificationClient.build(
                    permit.getTenantId(), userId, requestId,
                    "WKP_TIMEOUT_WARN", "WORK_PERMIT", permit.getId(), variables);
            notificationClient.send(request);
        }
    }
}
