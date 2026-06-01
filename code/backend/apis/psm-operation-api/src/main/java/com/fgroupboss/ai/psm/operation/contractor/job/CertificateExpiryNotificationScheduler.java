package com.fgroupboss.ai.psm.operation.contractor.job;

import com.fgroupboss.ai.psm.common.notification.CentralNotificationClient;
import com.fgroupboss.ai.psm.common.notification.NotificationSendRequest;
import com.fgroupboss.ai.psm.operation.contractor.mapper.ContractorWorkerMapper;
import com.fgroupboss.ai.psm.operation.contractor.mapper.WorkerCertificateMapper;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.ContractorWorkerEntity;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.WorkerCertificateEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扫描即将到期证书并发送站内信（best-effort）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CertificateExpiryNotificationScheduler {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final int[] REMIND_DAYS = new int[]{30, 7, 1, 0};

    private final WorkerCertificateMapper certificateMapper;
    private final ContractorWorkerMapper workerMapper;
    private final CentralNotificationClient notificationClient;

    @Value("${psm.cert-expiry-notify-user-id:1}")
    private Long defaultNotifyUserId;

    @Scheduled(cron = "${psm.cert-expiry-cron:0 0 8 * * ?}")
    public void scanExpiringCertificates() {
        LocalDate today = LocalDate.now();
        for (int days : REMIND_DAYS) {
            LocalDate target = today.plusDays(days);
            List<WorkerCertificateEntity> certificates = certificateMapper.listExpiringOn(target);
            for (WorkerCertificateEntity cert : certificates) {
                notifyIfNeeded(cert, days, target);
            }
        }
    }

    private void notifyIfNeeded(WorkerCertificateEntity cert, int daysBefore, LocalDate validTo) {
        Long userId = defaultNotifyUserId;
        if (userId == null) {
            return;
        }
        ContractorWorkerEntity worker = workerMapper.selectById(cert.getWorkerId());
        String workerName = worker == null ? String.valueOf(cert.getWorkerId()) : worker.getName();
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("workerName", workerName);
        variables.put("certType", StringUtils.hasText(cert.getCertType()) ? cert.getCertType() : "CERT");
        variables.put("validTo", validTo.format(DATE_FMT));
        String requestId = "CERT-EXP-" + cert.getId() + "-D" + daysBefore;
        NotificationSendRequest request = CentralNotificationClient.build(
                cert.getTenantId(), userId, requestId,
                "CERT_EXPIRE_WARN", "WORKER_CERTIFICATE", cert.getId(), variables);
        notificationClient.send(request);
    }
}
