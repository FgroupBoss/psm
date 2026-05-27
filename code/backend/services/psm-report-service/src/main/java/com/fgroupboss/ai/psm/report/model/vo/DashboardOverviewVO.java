package com.fgroupboss.ai.psm.report.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class DashboardOverviewVO {

    private WorkPermitReportSummaryVO workPermits;
    private AlarmReportSummaryVO alarms;
    private MajorHazardReportSummaryVO majorHazards;
    private ContractorReportSummaryVO contractors;
    private AuditReportSummaryVO audit;
    private Date refreshedAt;
    private String dataSourceNote;
}
