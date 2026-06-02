package com.fgroupboss.ai.psm.incidentgovernance.report.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
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
 * Acceptance 模块 HTTP API。
 * <p>基础路径：{@code /api/acceptance}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/acceptance")
public class AcceptanceController {

    private final ReportService reportService;

    /**
     * 查询test cases。
     * <p>HTTP GET {@code /api/acceptance/test-cases}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param module module 参数
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/test-cases")
    public ResponseVO<PageResult<AcceptanceTestCaseVO>> pageTestCases(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) String module,
                                                                      @RequestParam(required = false) String status,
                                                                      @RequestParam(defaultValue = "1") int pageNo,
                                                                      @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(reportService.pageTestCases(loginContext.getTenantId(), module, status, pageNo, pageSize));
    }

    /**
     * 新增test cases或触发test cases相关动作。
     * <p>HTTP POST {@code /api/acceptance/test-cases}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/test-cases")
    public ResponseVO<AcceptanceTestCaseVO> createTestCase(@Valid @RequestBody AcceptanceTestCaseRequest request) {
        return ResponseVO.success(reportService.createTestCase(request));
    }

    /**
     * 查询test runs。
     * <p>HTTP GET {@code /api/acceptance/test-runs}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param caseId case ID
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/test-runs")
    public ResponseVO<PageResult<AcceptanceTestRunVO>> pageTestRuns(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) Long caseId,
                                                                    @RequestParam(defaultValue = "1") int pageNo,
                                                                    @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(reportService.pageTestRuns(loginContext.getTenantId(), caseId, pageNo, pageSize));
    }

    /**
     * 新增test runs或触发test runs相关动作。
     * <p>HTTP POST {@code /api/acceptance/test-runs}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/test-runs")
    public ResponseVO<AcceptanceTestRunVO> createTestRun(@Valid @RequestBody AcceptanceTestRunRequest request) {
        return ResponseVO.success(reportService.createTestRun(request));
    }
}
