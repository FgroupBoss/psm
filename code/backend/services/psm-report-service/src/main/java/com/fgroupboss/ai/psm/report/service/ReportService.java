package com.fgroupboss.ai.psm.report.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.report.model.dto.AcceptanceTestCaseRequest;
import com.fgroupboss.ai.psm.report.model.dto.AcceptanceTestRunRequest;
import com.fgroupboss.ai.psm.report.model.dto.ReportExportRequest;
import com.fgroupboss.ai.psm.report.model.vo.AcceptanceTestCaseVO;
import com.fgroupboss.ai.psm.report.model.vo.AcceptanceTestRunVO;
import com.fgroupboss.ai.psm.report.model.vo.AlarmReportDetailVO;
import com.fgroupboss.ai.psm.report.model.vo.AlarmReportSummaryVO;
import com.fgroupboss.ai.psm.report.model.vo.AuditReportSummaryVO;
import com.fgroupboss.ai.psm.report.model.vo.ContractorReportSummaryVO;
import com.fgroupboss.ai.psm.report.model.vo.DashboardOverviewVO;
import com.fgroupboss.ai.psm.report.model.vo.MajorHazardReportSummaryVO;
import com.fgroupboss.ai.psm.report.model.vo.ReportExportTaskVO;
import com.fgroupboss.ai.psm.report.model.vo.ReportHealthVO;
import com.fgroupboss.ai.psm.report.model.vo.TrendSeriesVO;
import com.fgroupboss.ai.psm.report.model.vo.WorkPermitReportDetailVO;
import com.fgroupboss.ai.psm.report.model.vo.WorkPermitReportSummaryVO;

import java.util.List;

/**
 * M08 报表、大屏与 UAT 验收服务。
 */
public interface ReportService {

    ReportHealthVO health();

    WorkPermitReportSummaryVO workPermitSummary(Long tenantId);

    List<WorkPermitReportDetailVO> workPermitDetails(Long tenantId, String status, String workType);

    AlarmReportSummaryVO alarmSummary(Long tenantId);

    List<AlarmReportDetailVO> alarmDetails(Long tenantId, String status, String alarmLevel);

    MajorHazardReportSummaryVO majorHazardSummary(Long tenantId);

    ContractorReportSummaryVO contractorSummary(Long tenantId);

    AuditReportSummaryVO auditSummary(Long tenantId);

    DashboardOverviewVO dashboardOverview(Long tenantId);

    TrendSeriesVO workPermitTrend(Long tenantId, int days);

    TrendSeriesVO alarmTrend(Long tenantId, int days);

    ReportExportTaskVO createExport(ReportExportRequest request);

    ReportExportTaskVO getExportTask(Long tenantId, Long taskId);

    PageResult<AcceptanceTestCaseVO> pageTestCases(Long tenantId, String module, String status,
                                                   int pageNo, int pageSize);

    AcceptanceTestCaseVO createTestCase(AcceptanceTestCaseRequest request);

    PageResult<AcceptanceTestRunVO> pageTestRuns(Long tenantId, Long caseId, int pageNo, int pageSize);

    AcceptanceTestRunVO createTestRun(AcceptanceTestRunRequest request);
}
