package com.fgroupboss.ai.psm.incidentgovernance.integration.controller.reg;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.dto.RegReportPreviewRequest;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.dto.RegReportTriggerRequest;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReconciliationVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReportPreviewVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReportReceiptVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReportTaskVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.service.RegReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 监管上报任务、回执、对账与预览接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/integration/reg")
public class RegReportController {

    private final RegReportService reportService;

    @GetMapping("/tasks")
    public ResponseVO<PageResult<RegReportTaskVO>> pageTasks(@RequestParam Long tenantId,
                                                             @RequestParam(required = false) String platformCode,
                                                             @RequestParam(required = false) String dataDomain,
                                                             @RequestParam(required = false) String status,
                                                             @RequestParam(defaultValue = "1") int pageNo,
                                                             @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(reportService.pageTasks(tenantId, platformCode, dataDomain, status, pageNo, pageSize));
    }

    @PostMapping("/tasks/trigger")
    public ResponseVO<RegReportTaskVO> trigger(@Valid @RequestBody RegReportTriggerRequest request) {
        return ResponseVO.success(reportService.trigger(request));
    }

    @PostMapping("/tasks/{id}/retry")
    public ResponseVO<RegReportTaskVO> retry(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(reportService.retry(tenantId, id));
    }

    @GetMapping("/receipts")
    public ResponseVO<PageResult<RegReportReceiptVO>> pageReceipts(@RequestParam Long tenantId,
                                                                   @RequestParam(required = false) Long taskId,
                                                                   @RequestParam(defaultValue = "1") int pageNo,
                                                                   @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(reportService.pageReceipts(tenantId, taskId, pageNo, pageSize));
    }

    @GetMapping("/reconciliation")
    public ResponseVO<RegReconciliationVO> reconciliation(@RequestParam Long tenantId,
                                                          @RequestParam(required = false) String platformCode,
                                                          @RequestParam(required = false) String dataDomain) {
        return ResponseVO.success(reportService.reconciliation(tenantId, platformCode, dataDomain));
    }

    @PostMapping("/preview")
    public ResponseVO<RegReportPreviewVO> preview(@Valid @RequestBody RegReportPreviewRequest request) {
        return ResponseVO.success(reportService.preview(request));
    }
}
