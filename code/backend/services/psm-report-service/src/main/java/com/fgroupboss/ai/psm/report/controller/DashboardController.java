package com.fgroupboss.ai.psm.report.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.report.model.vo.DashboardOverviewVO;
import com.fgroupboss.ai.psm.report.model.vo.TrendSeriesVO;
import com.fgroupboss.ai.psm.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * M08 大屏态势接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ReportService reportService;

    @GetMapping("/overview")
    public ResponseVO<DashboardOverviewVO> overview(@RequestParam Long tenantId) {
        return ResponseVO.success(reportService.dashboardOverview(tenantId));
    }

    @GetMapping("/work-permits/trend")
    public ResponseVO<TrendSeriesVO> workPermitTrend(@RequestParam Long tenantId,
                                                     @RequestParam(defaultValue = "7") int days) {
        return ResponseVO.success(reportService.workPermitTrend(tenantId, days));
    }

    @GetMapping("/alarms/trend")
    public ResponseVO<TrendSeriesVO> alarmTrend(@RequestParam Long tenantId,
                                                @RequestParam(defaultValue = "7") int days) {
        return ResponseVO.success(reportService.alarmTrend(tenantId, days));
    }
}
