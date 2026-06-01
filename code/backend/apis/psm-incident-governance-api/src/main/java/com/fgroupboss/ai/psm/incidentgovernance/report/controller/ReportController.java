package com.fgroupboss.ai.psm.incidentgovernance.report.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.dto.ReportExportRequest;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.AlarmReportDetailVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.AlarmReportSummaryVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.AuditReportSummaryVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.ContractorReportSummaryVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.MajorHazardReportSummaryVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.ReportExportTaskVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.ReportHealthVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.WorkPermitReportDetailVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.WorkPermitReportSummaryVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.service.ReportService;
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
 * Report 模块 HTTP API。
 * <p>基础路径：{@code /api/reports}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    /**
     * 服务健康检查。
     * <p>HTTP GET {@code /api/reports/health}</p>
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/health")
    public ResponseVO<ReportHealthVO> health() {
        return ResponseVO.success(reportService.health());
    }

    /**
     * 查询summary。
     * <p>HTTP GET {@code /api/reports/work-permits/summary}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/work-permits/summary")
    public ResponseVO<WorkPermitReportSummaryVO> workPermitSummary(@RequestParam Long tenantId) {
        return ResponseVO.success(reportService.workPermitSummary(tenantId));
    }

    /**
     * 查询details。
     * <p>HTTP GET {@code /api/reports/work-permits/details}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param status 业务状态筛选
     * @param workType 作业类型编码
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/work-permits/details")
    public ResponseVO<List<WorkPermitReportDetailVO>> workPermitDetails(@RequestParam Long tenantId,
                                                                        @RequestParam(required = false) String status,
                                                                        @RequestParam(required = false) String workType) {
        return ResponseVO.success(reportService.workPermitDetails(tenantId, status, workType));
    }

    /**
     * 查询summary。
     * <p>HTTP GET {@code /api/reports/alarms/summary}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/alarms/summary")
    public ResponseVO<AlarmReportSummaryVO> alarmSummary(@RequestParam Long tenantId) {
        return ResponseVO.success(reportService.alarmSummary(tenantId));
    }

    /**
     * 查询details。
     * <p>HTTP GET {@code /api/reports/alarms/details}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param status 业务状态筛选
     * @param alarmLevel alarmLevel 参数
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/alarms/details")
    public ResponseVO<List<AlarmReportDetailVO>> alarmDetails(@RequestParam Long tenantId,
                                                              @RequestParam(required = false) String status,
                                                              @RequestParam(required = false) String alarmLevel) {
        return ResponseVO.success(reportService.alarmDetails(tenantId, status, alarmLevel));
    }

    /**
     * 查询summary。
     * <p>HTTP GET {@code /api/reports/major-hazards/summary}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/major-hazards/summary")
    public ResponseVO<MajorHazardReportSummaryVO> majorHazardSummary(@RequestParam Long tenantId) {
        return ResponseVO.success(reportService.majorHazardSummary(tenantId));
    }

    /**
     * 查询summary。
     * <p>HTTP GET {@code /api/reports/contractors/summary}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/contractors/summary")
    public ResponseVO<ContractorReportSummaryVO> contractorSummary(@RequestParam Long tenantId) {
        return ResponseVO.success(reportService.contractorSummary(tenantId));
    }

    /**
     * 查询summary。
     * <p>HTTP GET {@code /api/reports/audit/summary}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/audit/summary")
    public ResponseVO<AuditReportSummaryVO> auditSummary(@RequestParam Long tenantId) {
        return ResponseVO.success(reportService.auditSummary(tenantId));
    }

    /**
     * 新增export或触发export相关动作。
     * <p>HTTP POST {@code /api/reports/export}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/export")
    public ResponseVO<ReportExportTaskVO> exportReport(@Valid @RequestBody ReportExportRequest request) {
        return ResponseVO.success(reportService.createExport(request));
    }

    /**
     * 查询export。
     * <p>HTTP GET {@code /api/reports/export/{taskId}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param taskId task ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/export/{taskId}")
    public ResponseVO<ReportExportTaskVO> getExportTask(@PathVariable Long taskId, @RequestParam Long tenantId) {
        return ResponseVO.success(reportService.getExportTask(tenantId, taskId));
    }

    /**
     * 查询download。
     * <p>HTTP GET {@code /api/reports/export/{taskId}/download}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param taskId task ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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
