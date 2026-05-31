package com.fgroupboss.ai.psm.risk.inspection.job;

import com.fgroupboss.ai.psm.risk.inspection.service.InspectionPlanScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时扫描启用计划并生成待执行任务（DAILY/WEEKLY）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InspectionPlanScheduler {

    private final InspectionPlanScheduleService scheduleService;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void generateTasks() {
        try {
            scheduleService.generateDueTasks();
        } catch (Exception ex) {
            log.error("inspection plan task generation failed", ex);
        }
    }
}
