package com.fgroupboss.ai.psm.incidentgovernance.integration.job;

import com.fgroupboss.ai.psm.incidentgovernance.integration.config.RegDataDomain;
import com.fgroupboss.ai.psm.incidentgovernance.integration.config.RegTriggerType;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.dto.RegReportTriggerRequest;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegPlatformConfigVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.service.RegPlatformConfigService;
import com.fgroupboss.ai.psm.incidentgovernance.integration.service.RegReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 每日 02:00 为已启用监管平台触发四域上报任务。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegReportScheduler {

    private static final List<String> SCHEDULED_DOMAINS = Arrays.asList(
            RegDataDomain.DUAL_PREVENTION,
            RegDataDomain.WORK_PERMIT,
            RegDataDomain.MAJOR_HAZARD,
            RegDataDomain.ALARM);

    private final RegPlatformConfigService platformConfigService;
    private final RegReportService reportService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void triggerEnabledPlatformsDaily() {
        List<RegPlatformConfigVO> platforms = platformConfigService.listAllEnabled();
        if (platforms.isEmpty()) {
            log.debug("reg daily schedule skipped: no enabled platform");
            return;
        }
        LocalDateTime windowEnd = LocalDate.now().atStartOfDay();
        LocalDateTime windowStart = windowEnd.minusDays(1);
        for (RegPlatformConfigVO platform : platforms) {
            for (String dataDomain : SCHEDULED_DOMAINS) {
                triggerSafe(platform, dataDomain, windowStart, windowEnd);
            }
        }
    }

    private void triggerSafe(RegPlatformConfigVO platform, String dataDomain,
                             LocalDateTime windowStart, LocalDateTime windowEnd) {
        try {
            RegReportTriggerRequest request = new RegReportTriggerRequest();
            request.setTenantId(platform.getTenantId());
            request.setPlatformCode(platform.getPlatformCode());
            request.setDataDomain(dataDomain);
            request.setTriggerType(RegTriggerType.SCHEDULE.name());
            request.setDataWindowStart(windowStart);
            request.setDataWindowEnd(windowEnd);
            reportService.trigger(request);
            log.info("reg daily triggered tenantId={} platform={} domain={}",
                    platform.getTenantId(), platform.getPlatformCode(), dataDomain);
        } catch (Exception ex) {
            log.warn("reg daily trigger failed tenantId={} platform={} domain={} reason={}",
                    platform.getTenantId(), platform.getPlatformCode(), dataDomain, ex.getMessage());
        }
    }
}
