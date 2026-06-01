package com.fgroupboss.ai.psm.incidentgovernance.report.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.Phase3ReportSummaryVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.service.Phase3ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Phase3Report 模块 HTTP API。
 * <p>基础路径：{@code /api/reports/phase3}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports/phase3")
public class Phase3ReportController {

    private final Phase3ReportService phase3ReportService;

    /**
     * 查询summary。
     * <p>HTTP GET {@code /api/reports/phase3/summary}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/summary")
    public ResponseVO<Phase3ReportSummaryVO> summary(@RequestParam Long tenantId) {
        return ResponseVO.success(phase3ReportService.summary(tenantId));
    }
}
