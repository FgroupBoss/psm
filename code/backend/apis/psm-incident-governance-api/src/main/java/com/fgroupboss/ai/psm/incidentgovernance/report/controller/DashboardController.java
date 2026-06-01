package com.fgroupboss.ai.psm.incidentgovernance.report.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.DashboardOverviewVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.TrendSeriesVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard 模块 HTTP API。
 * <p>基础路径：{@code /api/dashboard}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ReportService reportService;

    /**
     * 查询overview。
     * <p>HTTP GET {@code /api/dashboard/overview}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/overview")
    public ResponseVO<DashboardOverviewVO> overview(@RequestParam Long tenantId) {
        return ResponseVO.success(reportService.dashboardOverview(tenantId));
    }

    /**
     * 查询trend。
     * <p>HTTP GET {@code /api/dashboard/work-permits/trend}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param days days 参数
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/work-permits/trend")
    public ResponseVO<TrendSeriesVO> workPermitTrend(@RequestParam Long tenantId,
                                                     @RequestParam(defaultValue = "7") int days) {
        return ResponseVO.success(reportService.workPermitTrend(tenantId, days));
    }

    /**
     * 查询trend。
     * <p>HTTP GET {@code /api/dashboard/alarms/trend}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param days days 参数
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/alarms/trend")
    public ResponseVO<TrendSeriesVO> alarmTrend(@RequestParam Long tenantId,
                                                @RequestParam(defaultValue = "7") int days) {
        return ResponseVO.success(reportService.alarmTrend(tenantId, days));
    }
}
