package com.fgroupboss.ai.psm.incidentgovernance.report.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.dto.AcceptanceTestCaseRequest;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.dto.AcceptanceTestRunRequest;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.AcceptanceTestCaseVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.AcceptanceTestRunVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * M08 UAT 验收用例与执行记录接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/acceptance")
public class AcceptanceController {

    private final ReportService reportService;

    /**
     * 接口用途：分页查询验收用例。
     */
    @GetMapping("/test-cases")
    public ResponseVO<PageResult<AcceptanceTestCaseVO>> pageTestCases(@RequestParam Long tenantId,
                                                                      @RequestParam(required = false) String module,
                                                                      @RequestParam(required = false) String status,
                                                                      @RequestParam(defaultValue = "1") int pageNo,
                                                                      @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(reportService.pageTestCases(tenantId, module, status, pageNo, pageSize));
    }

    /**
     * 接口用途：创建验收用例。
     */
    @PostMapping("/test-cases")
    public ResponseVO<AcceptanceTestCaseVO> createTestCase(@Valid @RequestBody AcceptanceTestCaseRequest request) {
        return ResponseVO.success(reportService.createTestCase(request));
    }

    /**
     * 接口用途：分页查询验收执行记录。
     */
    @GetMapping("/test-runs")
    public ResponseVO<PageResult<AcceptanceTestRunVO>> pageTestRuns(@RequestParam Long tenantId,
                                                                    @RequestParam(required = false) Long caseId,
                                                                    @RequestParam(defaultValue = "1") int pageNo,
                                                                    @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(reportService.pageTestRuns(tenantId, caseId, pageNo, pageSize));
    }

    /**
     * 接口用途：创建验收执行记录。
     */
    @PostMapping("/test-runs")
    public ResponseVO<AcceptanceTestRunVO> createTestRun(@Valid @RequestBody AcceptanceTestRunRequest request) {
        return ResponseVO.success(reportService.createTestRun(request));
    }
}
