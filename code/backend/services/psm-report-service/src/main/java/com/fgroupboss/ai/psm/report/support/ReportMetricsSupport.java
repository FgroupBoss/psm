package com.fgroupboss.ai.psm.report.support;

import com.fgroupboss.ai.psm.report.client.dto.RemoteAlarmActionVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteAlarmDetailVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteAlarmEventVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteAuditLogVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteContractorCompanyVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteContractorWorkerVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteGasTestVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteMajorHazardVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteSafetyMeasureVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteSiteConfirmVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteWorkPermitDetailVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteWorkPermitVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteWorkPermitWorkerVO;
import com.fgroupboss.ai.psm.report.model.vo.AlarmReportDetailVO;
import com.fgroupboss.ai.psm.report.model.vo.AlarmReportSummaryVO;
import com.fgroupboss.ai.psm.report.model.vo.AuditReportSummaryVO;
import com.fgroupboss.ai.psm.report.model.vo.ContractorReportSummaryVO;
import com.fgroupboss.ai.psm.report.model.vo.MajorHazardReportSummaryVO;
import com.fgroupboss.ai.psm.report.model.vo.TrendPointVO;
import com.fgroupboss.ai.psm.report.model.vo.WorkPermitReportDetailVO;
import com.fgroupboss.ai.psm.report.model.vo.WorkPermitReportSummaryVO;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * M08 指标口径计算：公式见各方法注释。
 */
public final class ReportMetricsSupport {

    private static final Set<String> SITE_PERMIT_STATUSES = new HashSet<String>(Arrays.asList(
            "PENDING_SITE_PERMIT", "IN_PROGRESS", "SUSPENDED", "PENDING_ACCEPTANCE", "CLOSED"));
    private static final Set<String> CRITICAL_ACTIONS = new HashSet<String>(Arrays.asList(
            "APPROVE", "REJECT", "SITE_PERMIT", "CLOSE", "CONFIRM", "DISPATCH", "PUBLISH", "REPORT_EXPORT"));

    private ReportMetricsSupport() {
    }

    public static WorkPermitReportSummaryVO buildWorkPermitSummary(List<RemoteWorkPermitVO> permits,
                                                                     List<WorkPermitReportDetailVO> tracedDetails,
                                                                     int offlineWorkCount,
                                                                     Date refreshedAt,
                                                                     String dataSource) {
        WorkPermitReportSummaryVO summary = new WorkPermitReportSummaryVO();
        summary.setTotalCount(permits.size());
        summary.setDataRefreshedAt(refreshedAt);
        summary.setDataSource(dataSource);

        Map<String, Long> statusCounts = new LinkedHashMap<String, Long>();
        Map<String, Long> workTypeCounts = new LinkedHashMap<String, Long>();
        long requiringSite = 0;
        long suspended = 0;
        long overdue = 0;
        long contractorInvolved = 0;
        long contractorChecked = 0;
        long traced = 0;
        Date now = refreshedAt == null ? new Date() : refreshedAt;

        for (WorkPermitReportDetailVO detail : tracedDetails) {
            if (detail.isSitePermitRequired()) {
                requiringSite++;
            }
            if (detail.isSiteTraceComplete()) {
                traced++;
            }
            if (detail.isContractorInvolved()) {
                contractorInvolved++;
            }
            if (detail.isContractorEligibilityChecked()) {
                contractorChecked++;
            }
        }

        for (RemoteWorkPermitVO permit : permits) {
            increment(statusCounts, permit.getStatus());
            increment(workTypeCounts, permit.getWorkType());
            if ("SUSPENDED".equalsIgnoreCase(permit.getStatus())) {
                suspended++;
            }
            if (isOverdue(permit, now)) {
                overdue++;
            }
        }

        summary.setStatusCounts(statusCounts);
        summary.setWorkTypeCounts(workTypeCounts);
        summary.setRequiringSitePermitCount(requiringSite);
        summary.setTracedPermitCount(traced);
        summary.setContractorInvolvedCount(contractorInvolved);
        summary.setContractorCheckedCount(contractorChecked);
        summary.setSuspendedCount(suspended);
        summary.setOverdueCount(overdue);

        // 特殊作业系统办理率 = 系统内作业票数量 / (系统内 + 线下台账)
        long systemCount = permits.size();
        long denominator = systemCount + Math.max(offlineWorkCount, 0);
        summary.setSystemProcessingRate(rate(systemCount, denominator));

        // 现场许可留痕率 = 有签到/检测/措施/签名的作业票 / 需现场许可作业票
        summary.setSiteTraceRate(rate(traced, requiringSite));

        // 承包商资质校验覆盖率 = 执行 eligibility 的承包商作业票 / 涉及承包商作业票
        summary.setContractorEligibilityCoverageRate(rate(contractorChecked, contractorInvolved));
        return summary;
    }

    public static WorkPermitReportDetailVO toWorkPermitDetail(Long tenantId,
                                                              RemoteWorkPermitVO permit,
                                                              RemoteWorkPermitDetailVO detail) {
        WorkPermitReportDetailVO vo = new WorkPermitReportDetailVO();
        vo.setId(permit.getId());
        vo.setPermitNo(permit.getPermitNo());
        vo.setWorkType(permit.getWorkType());
        vo.setStatus(permit.getStatus());
        vo.setTitle(permit.getTitle());
        vo.setAreaId(permit.getAreaId());
        vo.setContractorCompanyId(permit.getContractorCompanyId());
        vo.setPlanStartAt(permit.getPlanStartAt());
        vo.setPlanEndAt(permit.getPlanEndAt());
        vo.setCreatedAt(permit.getCreatedAt());

        boolean siteRequired = requiresSitePermit(permit.getStatus());
        vo.setSitePermitRequired(siteRequired);
        vo.setSiteTraceComplete(siteRequired && hasSiteTrace(detail));
        boolean contractorInvolved = permit.getContractorCompanyId() != null || hasContractorWorker(detail);
        vo.setContractorInvolved(contractorInvolved);
        vo.setContractorEligibilityChecked(contractorInvolved && hasContractorWorker(detail));
        return vo;
    }

    public static AlarmReportSummaryVO buildAlarmSummary(List<RemoteAlarmEventVO> alarms,
                                                         List<AlarmReportDetailVO> details,
                                                         Date refreshedAt,
                                                         String dataSource) {
        AlarmReportSummaryVO summary = new AlarmReportSummaryVO();
        summary.setTotalCount(alarms.size());
        summary.setDataRefreshedAt(refreshedAt);
        summary.setDataSource(dataSource);

        Map<String, Long> levelCounts = new LinkedHashMap<String, Long>();
        Map<String, Long> statusCounts = new LinkedHashMap<String, Long>();
        long closed = 0;
        long falseClosed = 0;
        long unconfirmed = 0;
        long inProgress = 0;

        for (RemoteAlarmEventVO alarm : alarms) {
            increment(levelCounts, alarm.getAlarmLevel());
            increment(statusCounts, alarm.getStatus());
            if ("CLOSED".equalsIgnoreCase(alarm.getStatus())) {
                closed++;
            }
            if ("FALSE_CLOSED".equalsIgnoreCase(alarm.getStatus())) {
                falseClosed++;
            }
            if ("NEW".equalsIgnoreCase(alarm.getStatus())) {
                unconfirmed++;
            }
            if ("IN_PROGRESS".equalsIgnoreCase(alarm.getStatus())
                    || "CONFIRMED".equalsIgnoreCase(alarm.getStatus())
                    || "ESCALATED".equalsIgnoreCase(alarm.getStatus())) {
                inProgress++;
            }
        }

        long actionable = alarms.size() - falseClosed;
        summary.setLevelCounts(levelCounts);
        summary.setStatusCounts(statusCounts);
        summary.setClosedCount(closed);
        summary.setFalseClosedCount(falseClosed);
        summary.setUnconfirmedCount(unconfirmed);
        summary.setInProgressCount(inProgress);
        summary.setActionableCount(actionable);

        // 报警闭环率 = 已关闭报警 / 应处置报警（排除误报关闭）
        summary.setClosureRate(rate(closed, actionable));
        summary.setFalseAlarmRate(rate(falseClosed, alarms.size()));

        long confirmTotal = 0;
        long disposeTotal = 0;
        int confirmSamples = 0;
        int disposeSamples = 0;
        for (AlarmReportDetailVO detail : details) {
            if (detail.getConfirmDurationMinutes() != null) {
                confirmTotal += detail.getConfirmDurationMinutes();
                confirmSamples++;
            }
            if (detail.getDisposeDurationMinutes() != null) {
                disposeTotal += detail.getDisposeDurationMinutes();
                disposeSamples++;
            }
        }
        if (confirmSamples > 0) {
            summary.setAvgConfirmMinutes((double) confirmTotal / confirmSamples);
        }
        if (disposeSamples > 0) {
            summary.setAvgDisposeMinutes((double) disposeTotal / disposeSamples);
        }
        return summary;
    }

    public static AlarmReportDetailVO toAlarmDetail(RemoteAlarmEventVO event, RemoteAlarmDetailVO detail) {
        AlarmReportDetailVO vo = new AlarmReportDetailVO();
        vo.setId(event.getId());
        vo.setAlarmNo(event.getAlarmNo());
        vo.setAlarmLevel(event.getAlarmLevel());
        vo.setStatus(event.getStatus());
        vo.setSourceType(event.getSourceType());
        vo.setAreaId(event.getAreaId());
        vo.setHazardId(event.getHazardId());
        vo.setFirstOccurredAt(event.getFirstOccurredAt());
        vo.setLastOccurredAt(event.getLastOccurredAt());

        Date confirmAt = findActionTime(detail, "CONFIRM");
        Date closeAt = findActionTime(detail, "CLOSE");
        if (confirmAt != null && event.getFirstOccurredAt() != null) {
            vo.setConfirmDurationMinutes(minutesBetween(event.getFirstOccurredAt(), confirmAt));
        }
        if (closeAt != null && confirmAt != null) {
            vo.setDisposeDurationMinutes(minutesBetween(confirmAt, closeAt));
        } else if (closeAt != null && event.getFirstOccurredAt() != null) {
            vo.setDisposeDurationMinutes(minutesBetween(event.getFirstOccurredAt(), closeAt));
        }
        return vo;
    }

    public static MajorHazardReportSummaryVO buildMajorHazardSummary(List<RemoteMajorHazardVO> hazards,
                                                                     Date refreshedAt,
                                                                     String dataSource) {
        MajorHazardReportSummaryVO summary = new MajorHazardReportSummaryVO();
        summary.setTotalCount(hazards.size());
        summary.setDataRefreshedAt(refreshedAt);
        summary.setDataSource(dataSource);

        Map<String, Long> levelCounts = new LinkedHashMap<String, Long>();
        Map<String, Long> statusCounts = new LinkedHashMap<String, Long>();
        long published = 0;
        long complete = 0;

        for (RemoteMajorHazardVO hazard : hazards) {
            increment(levelCounts, hazard.getLevel());
            increment(statusCounts, hazard.getStatus());
            if ("PUBLISHED".equalsIgnoreCase(hazard.getStatus()) || hazard.getPublishedAt() != null) {
                published++;
            }
            if (isArchiveComplete(hazard)) {
                complete++;
            }
        }

        summary.setLevelCounts(levelCounts);
        summary.setStatusCounts(statusCounts);
        summary.setPublishedCount(published);
        summary.setCompleteArchiveCount(complete);
        // 重大危险源档案完整率 = 必填完整危险源 / 试点危险源总数
        summary.setArchiveCompletenessRate(rate(complete, hazards.size()));
        return summary;
    }

    public static ContractorReportSummaryVO buildContractorSummary(List<RemoteContractorCompanyVO> companies,
                                                                   List<RemoteContractorWorkerVO> workers,
                                                                   Date refreshedAt,
                                                                   String dataSource) {
        ContractorReportSummaryVO summary = new ContractorReportSummaryVO();
        summary.setCompanyCount(companies.size());
        summary.setWorkerCount(workers.size());
        summary.setDataRefreshedAt(refreshedAt);
        summary.setDataSource(dataSource);

        Map<String, Long> companyStatusCounts = new LinkedHashMap<String, Long>();
        long approved = 0;
        long blacklist = 0;
        for (RemoteContractorCompanyVO company : companies) {
            increment(companyStatusCounts, company.getStatus());
            if ("APPROVED".equalsIgnoreCase(company.getStatus())) {
                approved++;
            }
            if (company.getBlacklistFlag() != null && company.getBlacklistFlag() == 1) {
                blacklist++;
            }
        }

        long certExpired = 0;
        long trainingInvalid = 0;
        long trainingQualified = 0;
        for (RemoteContractorWorkerVO worker : workers) {
            if ("EXPIRED".equalsIgnoreCase(worker.getCertificateStatus())) {
                certExpired++;
            }
            if ("INVALID".equalsIgnoreCase(worker.getTrainingStatus())) {
                trainingInvalid++;
            }
            if ("VALID".equalsIgnoreCase(worker.getTrainingStatus())) {
                trainingQualified++;
            }
        }

        summary.setCompanyStatusCounts(companyStatusCounts);
        summary.setApprovedCompanyCount(approved);
        summary.setBlacklistCount(blacklist);
        summary.setCertificateExpiredCount(certExpired);
        summary.setTrainingInvalidCount(trainingInvalid);
        summary.setTrainingQualifiedRate(rate(trainingQualified, workers.size()));
        return summary;
    }

    public static AuditReportSummaryVO buildAuditSummary(List<RemoteAuditLogVO> logs,
                                                         long criticalBaseline,
                                                         Date refreshedAt,
                                                         String dataSource) {
        AuditReportSummaryVO summary = new AuditReportSummaryVO();
        summary.setTotalLogCount(logs.size());
        summary.setCriticalOperationBaseline(criticalBaseline);
        summary.setDataRefreshedAt(refreshedAt);
        summary.setDataSource(dataSource);

        Map<String, Long> bizTypeCounts = new LinkedHashMap<String, Long>();
        long critical = 0;
        long configChange = 0;
        long permissionChange = 0;

        for (RemoteAuditLogVO log : logs) {
            increment(bizTypeCounts, log.getBizType());
            if (isCriticalAction(log.getAction())) {
                critical++;
            }
            if (log.getBizType() != null && log.getBizType().toUpperCase(Locale.ROOT).contains("CONFIG")) {
                configChange++;
            }
            if (log.getBizType() != null && log.getBizType().toUpperCase(Locale.ROOT).contains("PERMISSION")) {
                permissionChange++;
            }
        }

        summary.setBizTypeCounts(bizTypeCounts);
        summary.setCriticalActionLogCount(critical);
        summary.setConfigChangeCount(configChange);
        summary.setPermissionChangeCount(permissionChange);
        // 关键操作审计覆盖率 = 有审计记录关键操作 / 关键操作总数（基线可配置）
        summary.setCriticalAuditCoverageRate(rate(critical, criticalBaseline));
        return summary;
    }

    public static List<TrendPointVO> buildDailyTrend(List<Date> timestamps, int days, Date endDate) {
        if (days <= 0) {
            days = 7;
        }
        LocalDate end = toLocalDate(endDate == null ? new Date() : endDate);
        Map<String, Long> buckets = new LinkedHashMap<String, Long>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = days - 1; i >= 0; i--) {
            LocalDate day = end.minusDays(i);
            buckets.put(day.toString(), 0L);
        }
        for (Date timestamp : timestamps) {
            if (timestamp == null) {
                continue;
            }
            String key = format.format(timestamp);
            if (buckets.containsKey(key)) {
                buckets.put(key, buckets.get(key) + 1);
            }
        }
        List<TrendPointVO> points = new ArrayList<TrendPointVO>();
        for (Map.Entry<String, Long> entry : buckets.entrySet()) {
            TrendPointVO point = new TrendPointVO();
            point.setDateKey(entry.getKey());
            point.setCount(entry.getValue());
            points.add(point);
        }
        return points;
    }

    private static boolean requiresSitePermit(String status) {
        return status != null && SITE_PERMIT_STATUSES.contains(status.trim().toUpperCase(Locale.ROOT));
    }

    private static boolean hasSiteTrace(RemoteWorkPermitDetailVO detail) {
        if (detail == null) {
            return false;
        }
        boolean hasCheckIn = hasConfirmType(detail.getSiteConfirms(), "CHECK_IN", "CHECKIN", "SIGN_IN");
        boolean hasGasTest = detail.getGasTests() != null && !detail.getGasTests().isEmpty();
        boolean hasMeasure = hasConfirmedMeasure(detail.getSafetyMeasures());
        boolean hasSignature = hasConfirmType(detail.getSiteConfirms(), "SIGN", "SIGNATURE", "ACCEPTANCE");
        return hasCheckIn && hasGasTest && hasMeasure && hasSignature;
    }

    private static boolean hasConfirmType(List<RemoteSiteConfirmVO> confirms, String... types) {
        if (confirms == null || confirms.isEmpty()) {
            return false;
        }
        Set<String> expected = new HashSet<String>(Arrays.asList(types));
        for (RemoteSiteConfirmVO confirm : confirms) {
            if (confirm.getConfirmType() != null
                    && expected.contains(confirm.getConfirmType().trim().toUpperCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasConfirmedMeasure(List<RemoteSafetyMeasureVO> measures) {
        if (measures == null || measures.isEmpty()) {
            return false;
        }
        for (RemoteSafetyMeasureVO measure : measures) {
            if (Boolean.TRUE.equals(measure.getRequiredFlag())
                    && "CONFIRMED".equalsIgnoreCase(measure.getConfirmStatus())) {
                return true;
            }
        }
        for (RemoteSafetyMeasureVO measure : measures) {
            if ("CONFIRMED".equalsIgnoreCase(measure.getConfirmStatus())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasContractorWorker(RemoteWorkPermitDetailVO detail) {
        if (detail == null || detail.getWorkers() == null) {
            return false;
        }
        for (RemoteWorkPermitWorkerVO worker : detail.getWorkers()) {
            if ("CONTRACTOR".equalsIgnoreCase(worker.getWorkerType()) || worker.getCompanyId() != null) {
                return true;
            }
        }
        return false;
    }

    private static boolean isArchiveComplete(RemoteMajorHazardVO hazard) {
        return StringUtils.hasText(hazard.getName())
                && StringUtils.hasText(hazard.getLevel())
                && StringUtils.hasText(hazard.getHazardType())
                && hazard.getAreaId() != null
                && ("PUBLISHED".equalsIgnoreCase(hazard.getStatus()) || hazard.getPublishedAt() != null);
    }

    private static boolean isCriticalAction(String action) {
        return action != null && CRITICAL_ACTIONS.contains(action.trim().toUpperCase(Locale.ROOT));
    }

    private static boolean isOverdue(RemoteWorkPermitVO permit, Date now) {
        if (permit.getPlanEndAt() == null || permit.getStatus() == null) {
            return false;
        }
        if ("CLOSED".equalsIgnoreCase(permit.getStatus())) {
            return false;
        }
        return permit.getPlanEndAt().before(now);
    }

    private static Date findActionTime(RemoteAlarmDetailVO detail, String actionType) {
        if (detail == null || detail.getActions() == null) {
            return null;
        }
        for (RemoteAlarmActionVO action : detail.getActions()) {
            if (actionType.equalsIgnoreCase(action.getActionType())) {
                return action.getOperatedAt();
            }
        }
        return null;
    }

    private static long minutesBetween(Date start, Date end) {
        return ChronoUnit.MINUTES.between(start.toInstant(), end.toInstant());
    }

    private static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static void increment(Map<String, Long> map, String key) {
        String normalized = key == null ? "UNKNOWN" : key;
        Long current = map.get(normalized);
        map.put(normalized, current == null ? 1L : current + 1);
    }

    private static double rate(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0D;
        }
        return round4((double) numerator / denominator);
    }

    private static double round4(double value) {
        return Math.round(value * 10000D) / 10000D;
    }
}
