package com.fgroupboss.ai.psm.report.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.report.model.vo.Phase2ReportSummaryVO;
import com.fgroupboss.ai.psm.report.service.Phase2ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 二期跨模块聚合报表。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports/phase2")
public class Phase2ReportController {

    private final Phase2ReportService phase2ReportService;

    @GetMapping("/summary")
    public ResponseVO<Phase2ReportSummaryVO> summary(@RequestParam Long tenantId) {
        return ResponseVO.success(phase2ReportService.summary(tenantId));
    }
}
