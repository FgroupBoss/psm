package com.fgroupboss.ai.psm.report.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.report.model.dto.ReportExportRequest;
import com.fgroupboss.ai.psm.report.model.vo.AlarmReportDetailVO;
import com.fgroupboss.ai.psm.report.model.vo.AlarmReportSummaryVO;
import com.fgroupboss.ai.psm.report.model.vo.AuditReportSummaryVO;
import com.fgroupboss.ai.psm.report.model.vo.ContractorReportSummaryVO;
import com.fgroupboss.ai.psm.report.model.vo.MajorHazardReportSummaryVO;
import com.fgroupboss.ai.psm.report.model.vo.ReportExportTaskVO;
import com.fgroupboss.ai.psm.report.model.vo.ReportHealthVO;
import com.fgroupboss.ai.psm.report.model.vo.WorkPermitReportDetailVO;
import com.fgroupboss.ai.psm.report.model.vo.WorkPermitReportSummaryVO;
import com.fgroupboss.ai.psm.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * M08 统计报表接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/health")
    public ResponseVO<ReportHealthVO> health() {
        return ResponseVO.success(reportService.health());
    }

    @GetMapping("/work-permits/summary")
    public ResponseVO<WorkPermitReportSummaryVO> workPermitSummary(@RequestParam Long tenantId) {
        return ResponseVO.success(reportService.workPermitSummary(tenantId));
    }

    @GetMapping("/work-permits/details")
    public ResponseVO<List<WorkPermitReportDetailVO>> workPermitDetails(@RequestParam Long tenantId,
                                                                        @RequestParam(required = false) String status,
                                                                        @RequestParam(required = false) String workType) {
        return ResponseVO.success(reportService.workPermitDetails(tenantId, status, workType));
    }

    @GetMapping("/alarms/summary")
    public ResponseVO<AlarmReportSummaryVO> alarmSummary(@RequestParam Long tenantId) {
        return ResponseVO.success(reportService.alarmSummary(tenantId));
    }

    @GetMapping("/alarms/details")
    public ResponseVO<List<AlarmReportDetailVO>> alarmDetails(@RequestParam Long tenantId,
                                                              @RequestParam(required = false) String status,
                                                              @RequestParam(required = false) String alarmLevel) {
        return ResponseVO.success(reportService.alarmDetails(tenantId, status, alarmLevel));
    }

    @GetMapping("/major-hazards/summary")
    public ResponseVO<MajorHazardReportSummaryVO> majorHazardSummary(@RequestParam Long tenantId) {
        return ResponseVO.success(reportService.majorHazardSummary(tenantId));
    }

    @GetMapping("/contractors/summary")
    public ResponseVO<ContractorReportSummaryVO> contractorSummary(@RequestParam Long tenantId) {
        return ResponseVO.success(reportService.contractorSummary(tenantId));
    }

    @GetMapping("/audit/summary")
    public ResponseVO<AuditReportSummaryVO> auditSummary(@RequestParam Long tenantId) {
        return ResponseVO.success(reportService.auditSummary(tenantId));
    }

    @PostMapping("/export")
    public ResponseVO<ReportExportTaskVO> exportReport(@Valid @RequestBody ReportExportRequest request) {
        return ResponseVO.success(reportService.createExport(request));
    }

    @GetMapping("/export/{taskId}")
    public ResponseVO<ReportExportTaskVO> getExportTask(@PathVariable Long taskId, @RequestParam Long tenantId) {
        return ResponseVO.success(reportService.getExportTask(tenantId, taskId));
    }

    @GetMapping("/export/{taskId}/download")
    public ResponseEntity<Resource> downloadExport(@PathVariable Long taskId, @RequestParam Long tenantId) {
        Resource resource = reportService.loadExportFile(tenantId, taskId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + reportService.exportDownloadFileName(tenantId, taskId) + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}
