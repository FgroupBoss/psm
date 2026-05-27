package com.fgroupboss.ai.psm.alarm.job;

import com.fgroupboss.ai.psm.alarm.service.AlarmEscalationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时扫描超时报警并升级（批次 5，默认每分钟）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmEscalationScheduler {

    private final AlarmEscalationService escalationService;

    @Scheduled(cron = "0 * * * * ?")
    public void scanEscalations() {
        try {
            escalationService.processEscalations();
        } catch (Exception ex) {
            log.error("alarm escalation scan failed", ex);
        }
    }
}
