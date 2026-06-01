package com.fgroupboss.ai.psm.incidentgovernance.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.AlarmReportClient;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.AuditReportClient;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.ContractorReportClient;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.MajorHazardReportClient;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.WorkPermitReportClient;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.dto.RemoteAlarmDetailVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.dto.RemoteAlarmEventVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.dto.RemoteAuditLogVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.dto.RemoteContractorCompanyVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.dto.RemoteContractorWorkerVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.dto.RemoteMajorHazardVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.dto.RemoteWorkPermitDetailVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.dto.RemoteWorkPermitVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.mapper.AcceptanceTestCaseMapper;
import com.fgroupboss.ai.psm.incidentgovernance.report.mapper.AcceptanceTestRunMapper;
import com.fgroupboss.ai.psm.incidentgovernance.report.mapper.ReportExportTaskMapper;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.dto.AcceptanceTestCaseRequest;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.dto.AcceptanceTestRunRequest;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.dto.ReportExportRequest;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.entity.AcceptanceTestCaseEntity;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.entity.AcceptanceTestRunEntity;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.entity.ReportExportTaskEntity;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.AcceptanceTestCaseVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.AcceptanceTestRunVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.AlarmReportDetailVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.AlarmReportSummaryVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.AuditReportSummaryVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.ContractorReportSummaryVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.DashboardOverviewVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.MajorHazardReportSummaryVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.ReportExportTaskVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.ReportHealthVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.TrendPointVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.TrendSeriesVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.WorkPermitReportDetailVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.WorkPermitReportSummaryVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.service.ReportService;
import com.fgroupboss.ai.psm.incidentgovernance.report.support.ReportAuditSupport;
import com.fgroupboss.ai.psm.incidentgovernance.report.support.ReportCsvExportSupport;
import com.fgroupboss.ai.psm.incidentgovernance.report.support.ReportMetricsSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 实现方式：承载报表业务实现，基于 Mapper、远程客户端或支撑组件完成校验、状态流转和结果组装。
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final String DATA_SOURCE = "psm-report-service aggregated via RestTemplate";

    private final WorkPermitReportClient workPermitReportClient;
    private final AlarmReportClient alarmReportClient;
    private final MajorHazardReportClient majorHazardReportClient;
    private final ContractorReportClient contractorReportClient;
    private final AuditReportClient auditReportClient;
    private final ReportExportTaskMapper exportTaskMapper;
    private final AcceptanceTestCaseMapper testCaseMapper;
    private final AcceptanceTestRunMapper testRunMapper;
    private final ReportAuditSupport reportAuditSupport;
    private final ReportCsvExportSupport csvExportSupport;

    @Value("${psm.report.pilot-offline-work-count:0}")
    private int pilotOfflineWorkCount;

    @Value("${psm.report.critical-operation-baseline:100}")
    private long criticalOperationBaseline;

    /**
     * 实现方式：查询服务健康状态，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public ReportHealthVO health() {
        ReportHealthVO health = new ReportHealthVO();
        health.setService("psm-report-service");
        health.setModule("report");
        health.setVersion("1.0.0-m08");
        health.setExportTaskCount(exportTaskMapper.selectCount(null));
        health.setAcceptanceCaseCount(testCaseMapper.selectCount(null));
        health.setRefreshedAt(new Date());
        return health;
    }

    /**
     * 实现方式：统计作业票报表汇总，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public WorkPermitReportSummaryVO workPermitSummary(Long tenantId) {
        Date refreshedAt = new Date();
        List<RemoteWorkPermitVO> permits = workPermitReportClient.listPermits(tenantId);
        List<WorkPermitReportDetailVO> tracedDetails = buildWorkPermitDetails(tenantId, permits, null, null);
        return ReportMetricsSupport.buildWorkPermitSummary(permits, tracedDetails, pilotOfflineWorkCount,
                refreshedAt, DATA_SOURCE);
    }

    /**
     * 实现方式：查询作业票报表明细，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<WorkPermitReportDetailVO> workPermitDetails(Long tenantId, String status, String workType) {
        List<RemoteWorkPermitVO> permits = workPermitReportClient.listPermits(tenantId);
        return buildWorkPermitDetails(tenantId, permits, status, workType);
    }

    /**
     * 实现方式：统计报警报表汇总，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public AlarmReportSummaryVO alarmSummary(Long tenantId) {
        Date refreshedAt = new Date();
        List<RemoteAlarmEventVO> alarms = alarmReportClient.listAlarms(tenantId);
        List<AlarmReportDetailVO> details = buildAlarmDetails(tenantId, alarms, null, null);
        return ReportMetricsSupport.buildAlarmSummary(alarms, details, refreshedAt, DATA_SOURCE);
    }

    /**
     * 实现方式：查询报警报表明细，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<AlarmReportDetailVO> alarmDetails(Long tenantId, String status, String alarmLevel) {
        List<RemoteAlarmEventVO> alarms = alarmReportClient.listAlarms(tenantId);
        return buildAlarmDetails(tenantId, alarms, status, alarmLevel);
    }

    /**
     * 实现方式：统计重大危险源报表汇总，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public MajorHazardReportSummaryVO majorHazardSummary(Long tenantId) {
        List<RemoteMajorHazardVO> hazards = majorHazardReportClient.listHazards(tenantId);
        return ReportMetricsSupport.buildMajorHazardSummary(hazards, new Date(), DATA_SOURCE);
    }

    /**
     * 实现方式：统计承包商报表汇总，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public ContractorReportSummaryVO contractorSummary(Long tenantId) {
        List<RemoteContractorCompanyVO> companies = contractorReportClient.listCompanies(tenantId);
        List<RemoteContractorWorkerVO> workers = contractorReportClient.listWorkers(tenantId);
        return ReportMetricsSupport.buildContractorSummary(companies, workers, new Date(), DATA_SOURCE);
    }

    /**
     * 实现方式：统计审计报表汇总，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public AuditReportSummaryVO auditSummary(Long tenantId) {
        List<RemoteAuditLogVO> logs = auditReportClient.listLogs(tenantId);
        return ReportMetricsSupport.buildAuditSummary(logs, criticalOperationBaseline, new Date(), DATA_SOURCE);
    }

    /**
     * 实现方式：查询驾驶舱总览，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public DashboardOverviewVO dashboardOverview(Long tenantId) {
        DashboardOverviewVO overview = new DashboardOverviewVO();
        overview.setWorkPermits(workPermitSummary(tenantId));
        overview.setAlarms(alarmSummary(tenantId));
        overview.setMajorHazards(majorHazardSummary(tenantId));
        overview.setContractors(contractorSummary(tenantId));
        overview.setAudit(auditSummary(tenantId));
        overview.setRefreshedAt(new Date());
        overview.setDataSourceNote(DATA_SOURCE);
        return overview;
    }

    /**
     * 实现方式：查询作业票趋势，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public TrendSeriesVO workPermitTrend(Long tenantId, int days) {
        List<RemoteWorkPermitVO> permits = workPermitReportClient.listPermits(tenantId);
        List<Date> timestamps = new ArrayList<Date>();
        for (RemoteWorkPermitVO permit : permits) {
            timestamps.add(permit.getCreatedAt());
        }
        TrendSeriesVO series = new TrendSeriesVO();
        series.setMetric("WORK_PERMIT_CREATED");
        series.setDays(days);
        series.setPoints(ReportMetricsSupport.buildDailyTrend(timestamps, days, new Date()));
        series.setDataRefreshedAt(new Date());
        series.setDataSource(DATA_SOURCE);
        return series;
    }

    /**
     * 实现方式：查询报警趋势，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public TrendSeriesVO alarmTrend(Long tenantId, int days) {
        List<RemoteAlarmEventVO> alarms = alarmReportClient.listAlarms(tenantId);
        List<Date> timestamps = new ArrayList<Date>();
        for (RemoteAlarmEventVO alarm : alarms) {
            timestamps.add(alarm.getFirstOccurredAt());
        }
        TrendSeriesVO series = new TrendSeriesVO();
        series.setMetric("ALARM_OCCURRED");
        series.setDays(days);
        series.setPoints(ReportMetricsSupport.buildDailyTrend(timestamps, days, new Date()));
        series.setDataRefreshedAt(new Date());
        series.setDataSource(DATA_SOURCE);
        return series;
    }

    /**
     * 实现方式：创建报表导出任务，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public ReportExportTaskVO createExport(ReportExportRequest request) {
        Date now = new Date();
        ReportExportTaskEntity entity = new ReportExportTaskEntity();
        entity.setTenantId(request.getTenantId());
        entity.setReportType(request.getReportType().trim().toUpperCase(Locale.ROOT));
        entity.setExportFormat(StringUtils.hasText(request.getExportFormat()) ? request.getExportFormat() : "CSV");
        entity.setQueryParamsJson(request.getQueryParamsJson());
        entity.setRequestedBy(StringUtils.hasText(request.getRequestedBy()) ? request.getRequestedBy() : "system");
        entity.setStatus("PENDING");
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        exportTaskMapper.insert(entity);

        entity.setStartedAt(new Date());
        try {
            String storagePath = buildExportFile(entity);
            entity.setFilePath(storagePath);
            entity.setStatus("COMPLETED");
            entity.setCompletedAt(new Date());
        } catch (BusinessException ex) {
            entity.setStatus("FAILED");
            entity.setErrorMessage(ex.getMessage());
            entity.setCompletedAt(new Date());
        } catch (Exception ex) {
            entity.setStatus("FAILED");
            entity.setErrorMessage("export generation failed");
            entity.setCompletedAt(new Date());
        }
        entity.setUpdatedAt(entity.getCompletedAt());
        exportTaskMapper.updateById(entity);

        reportAuditSupport.auditExport(entity.getTenantId(), entity.getRequestedBy(), entity.getId(),
                entity.getReportType(), entity.getStatus());
        return toExportTaskVO(entity);
    }

    /**
     * 实现方式：查询报表导出任务，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public ReportExportTaskVO getExportTask(Long tenantId, Long taskId) {
        return toExportTaskVO(requireExportTask(tenantId, taskId));
    }

    /**
     * 实现方式：加载导出文件，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public Resource loadExportFile(Long tenantId, Long taskId) {
        ReportExportTaskEntity entity = requireExportTask(tenantId, taskId);
        if (!"COMPLETED".equalsIgnoreCase(entity.getStatus()) || !StringUtils.hasText(entity.getFilePath())) {
            throw new BusinessException(409, "export task is not ready for download");
        }
        return new FileSystemResource(csvExportSupport.resolve(entity.getFilePath()).toFile());
    }

    /**
     * 实现方式：生成导出文件名，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public String exportDownloadFileName(Long tenantId, Long taskId) {
        ReportExportTaskEntity entity = requireExportTask(tenantId, taskId);
        return entity.getReportType().toLowerCase(Locale.ROOT) + "-" + entity.getId() + ".csv";
    }

    private String buildExportFile(ReportExportTaskEntity entity) {
        Long tenantId = entity.getTenantId();
        String reportType = entity.getReportType();
        if ("WORK_PERMIT".equals(reportType)) {
            List<RemoteWorkPermitVO> permits = workPermitReportClient.listPermits(tenantId);
            return csvExportSupport.writeWorkPermits(tenantId, entity.getId(),
                    buildWorkPermitDetails(tenantId, permits, null, null));
        }
        if ("ALARM".equals(reportType)) {
            List<RemoteAlarmEventVO> alarms = alarmReportClient.listAlarms(tenantId);
            return csvExportSupport.writeAlarms(tenantId, entity.getId(), buildAlarmDetails(tenantId, alarms, null, null));
        }
        if ("CONTRACTOR".equals(reportType)) {
            ContractorReportSummaryVO summary = contractorSummary(tenantId);
            List<String[]> rows = new ArrayList<String[]>();
            rows.add(new String[]{"companyCount", String.valueOf(summary.getCompanyCount())});
            rows.add(new String[]{"workerCount", String.valueOf(summary.getWorkerCount())});
            rows.add(new String[]{"approvedCompanyCount", String.valueOf(summary.getApprovedCompanyCount())});
            rows.add(new String[]{"blacklistCount", String.valueOf(summary.getBlacklistCount())});
            return csvExportSupport.writeSummary(tenantId, entity.getId(), reportType, new String[]{"metric", "value"}, rows);
        }
        if ("MAJOR_HAZARD".equals(reportType)) {
            MajorHazardReportSummaryVO summary = majorHazardSummary(tenantId);
            List<String[]> rows = new ArrayList<String[]>();
            rows.add(new String[]{"totalCount", String.valueOf(summary.getTotalCount())});
            rows.add(new String[]{"publishedCount", String.valueOf(summary.getPublishedCount())});
            rows.add(new String[]{"archiveCompletenessRate", String.valueOf(summary.getArchiveCompletenessRate())});
            return csvExportSupport.writeSummary(tenantId, entity.getId(), reportType, new String[]{"metric", "value"}, rows);
        }
        throw new BusinessException(400, "unsupported report type: " + reportType);
    }

    private ReportExportTaskEntity requireExportTask(Long tenantId, Long taskId) {
        ReportExportTaskEntity entity = exportTaskMapper.selectById(taskId);
        if (entity == null || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "export task not found");
        }
        return entity;
    }

    /**
     * 实现方式：分页查询验收用例，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public PageResult<AcceptanceTestCaseVO> pageTestCases(Long tenantId, String module, String status,
                                                          int pageNo, int pageSize) {
        LambdaQueryWrapper<AcceptanceTestCaseEntity> wrapper = new LambdaQueryWrapper<AcceptanceTestCaseEntity>()
                .eq(AcceptanceTestCaseEntity::getTenantId, tenantId)
                .eq(StringUtils.hasText(module), AcceptanceTestCaseEntity::getModule, module)
                .eq(StringUtils.hasText(status), AcceptanceTestCaseEntity::getStatus, status)
                .orderByDesc(AcceptanceTestCaseEntity::getUpdatedAt);
        Page<AcceptanceTestCaseEntity> page = testCaseMapper.selectPage(new Page<AcceptanceTestCaseEntity>(pageNo, pageSize), wrapper);
        List<AcceptanceTestCaseVO> records = new ArrayList<AcceptanceTestCaseVO>();
        for (AcceptanceTestCaseEntity entity : page.getRecords()) {
            records.add(toTestCaseVO(entity));
        }
        return new PageResult<AcceptanceTestCaseVO>(page.getTotal(), pageNo, pageSize, records);
    }

    /**
     * 实现方式：创建验收用例，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public AcceptanceTestCaseVO createTestCase(AcceptanceTestCaseRequest request) {
        Date now = new Date();
        AcceptanceTestCaseEntity entity = new AcceptanceTestCaseEntity();
        entity.setTenantId(request.getTenantId());
        entity.setCaseCode(request.getCaseCode().trim());
        entity.setCaseName(request.getCaseName().trim());
        entity.setModule(request.getModule().trim().toUpperCase(Locale.ROOT));
        entity.setScenario(request.getScenario());
        entity.setExpectedResult(request.getExpectedResult());
        entity.setPriority(StringUtils.hasText(request.getPriority()) ? request.getPriority() : "P1");
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "ACTIVE");
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        testCaseMapper.insert(entity);
        return toTestCaseVO(entity);
    }

    /**
     * 实现方式：分页查询验收执行记录，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public PageResult<AcceptanceTestRunVO> pageTestRuns(Long tenantId, Long caseId, int pageNo, int pageSize) {
        LambdaQueryWrapper<AcceptanceTestRunEntity> wrapper = new LambdaQueryWrapper<AcceptanceTestRunEntity>()
                .eq(AcceptanceTestRunEntity::getTenantId, tenantId)
                .eq(caseId != null, AcceptanceTestRunEntity::getCaseId, caseId)
                .orderByDesc(AcceptanceTestRunEntity::getExecutedAt);
        Page<AcceptanceTestRunEntity> page = testRunMapper.selectPage(new Page<AcceptanceTestRunEntity>(pageNo, pageSize), wrapper);
        List<AcceptanceTestRunVO> records = new ArrayList<AcceptanceTestRunVO>();
        for (AcceptanceTestRunEntity entity : page.getRecords()) {
            records.add(toTestRunVO(entity));
        }
        return new PageResult<AcceptanceTestRunVO>(page.getTotal(), pageNo, pageSize, records);
    }

    /**
     * 实现方式：创建验收执行记录，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public AcceptanceTestRunVO createTestRun(AcceptanceTestRunRequest request) {
        AcceptanceTestCaseEntity testCase = testCaseMapper.selectById(request.getCaseId());
        if (testCase == null || !request.getTenantId().equals(testCase.getTenantId())) {
            throw new BusinessException(404, "acceptance test case not found");
        }
        Date now = new Date();
        AcceptanceTestRunEntity entity = new AcceptanceTestRunEntity();
        entity.setTenantId(request.getTenantId());
        entity.setCaseId(request.getCaseId());
        entity.setRunNo(request.getRunNo().trim());
        entity.setExecutorName(request.getExecutorName());
        entity.setRunStatus(request.getRunStatus().trim().toUpperCase(Locale.ROOT));
        entity.setEvidenceRef(request.getEvidenceRef());
        entity.setRemark(request.getRemark());
        entity.setExecutedAt(request.getExecutedAt() == null ? now : request.getExecutedAt());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        testRunMapper.insert(entity);
        return toTestRunVO(entity);
    }

    private List<WorkPermitReportDetailVO> buildWorkPermitDetails(Long tenantId,
                                                                  List<RemoteWorkPermitVO> permits,
                                                                  String status,
                                                                  String workType) {
        List<WorkPermitReportDetailVO> details = new ArrayList<WorkPermitReportDetailVO>();
        for (RemoteWorkPermitVO permit : permits) {
            if (StringUtils.hasText(status) && !status.equalsIgnoreCase(permit.getStatus())) {
                continue;
            }
            if (StringUtils.hasText(workType) && !workType.equalsIgnoreCase(permit.getWorkType())) {
                continue;
            }
            RemoteWorkPermitDetailVO remoteDetail = workPermitReportClient.getDetail(tenantId, permit.getId());
            details.add(ReportMetricsSupport.toWorkPermitDetail(tenantId, permit, remoteDetail));
        }
        return details;
    }

    private List<AlarmReportDetailVO> buildAlarmDetails(Long tenantId,
                                                        List<RemoteAlarmEventVO> alarms,
                                                        String status,
                                                        String alarmLevel) {
        List<AlarmReportDetailVO> details = new ArrayList<AlarmReportDetailVO>();
        for (RemoteAlarmEventVO alarm : alarms) {
            if (StringUtils.hasText(status) && !status.equalsIgnoreCase(alarm.getStatus())) {
                continue;
            }
            if (StringUtils.hasText(alarmLevel) && !alarmLevel.equalsIgnoreCase(alarm.getAlarmLevel())) {
                continue;
            }
            RemoteAlarmDetailVO remoteDetail = alarmReportClient.getDetail(tenantId, alarm.getId());
            details.add(ReportMetricsSupport.toAlarmDetail(alarm, remoteDetail));
        }
        return details;
    }

    private ReportExportTaskVO toExportTaskVO(ReportExportTaskEntity entity) {
        ReportExportTaskVO vo = new ReportExportTaskVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setReportType(entity.getReportType());
        vo.setExportFormat(entity.getExportFormat());
        vo.setStatus(entity.getStatus());
        vo.setFilePath(entity.getFilePath());
        if ("COMPLETED".equalsIgnoreCase(entity.getStatus()) && entity.getId() != null) {
            vo.setDownloadUrl("/api/reports/export/" + entity.getId() + "/download?tenantId=" + entity.getTenantId());
        }
        vo.setErrorMessage(entity.getErrorMessage());
        vo.setRequestedBy(entity.getRequestedBy());
        vo.setStartedAt(entity.getStartedAt());
        vo.setCompletedAt(entity.getCompletedAt());
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }

    private AcceptanceTestCaseVO toTestCaseVO(AcceptanceTestCaseEntity entity) {
        AcceptanceTestCaseVO vo = new AcceptanceTestCaseVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setCaseCode(entity.getCaseCode());
        vo.setCaseName(entity.getCaseName());
        vo.setModule(entity.getModule());
        vo.setScenario(entity.getScenario());
        vo.setExpectedResult(entity.getExpectedResult());
        vo.setPriority(entity.getPriority());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private AcceptanceTestRunVO toTestRunVO(AcceptanceTestRunEntity entity) {
        AcceptanceTestRunVO vo = new AcceptanceTestRunVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setCaseId(entity.getCaseId());
        vo.setRunNo(entity.getRunNo());
        vo.setExecutorName(entity.getExecutorName());
        vo.setRunStatus(entity.getRunStatus());
        vo.setEvidenceRef(entity.getEvidenceRef());
        vo.setRemark(entity.getRemark());
        vo.setExecutedAt(entity.getExecutedAt());
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }
}
