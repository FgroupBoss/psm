package com.fgroupboss.ai.psm.incidentgovernance.report.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.Phase2ReportSummaryVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.service.Phase2ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Phase2Report 模块 HTTP API。
 * <p>基础路径：{@code /api/reports/phase2}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports/phase2")
public class Phase2ReportController {

    private final Phase2ReportService phase2ReportService;

    /**
     * 查询summary。
     * <p>HTTP GET {@code /api/reports/phase2/summary}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/summary")
    public ResponseVO<Phase2ReportSummaryVO> summary(@RequestParam Long tenantId) {
        return ResponseVO.success(phase2ReportService.summary(tenantId));
    }
}
