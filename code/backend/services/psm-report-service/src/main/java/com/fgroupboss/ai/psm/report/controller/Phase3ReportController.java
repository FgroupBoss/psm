package com.fgroupboss.ai.psm.report.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.report.model.vo.Phase3ReportSummaryVO;
import com.fgroupboss.ai.psm.report.service.Phase3ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports/phase3")
public class Phase3ReportController {

    private final Phase3ReportService phase3ReportService;

    @GetMapping("/summary")
    public ResponseVO<Phase3ReportSummaryVO> summary(@RequestParam Long tenantId) {
        return ResponseVO.success(phase3ReportService.summary(tenantId));
    }
}
