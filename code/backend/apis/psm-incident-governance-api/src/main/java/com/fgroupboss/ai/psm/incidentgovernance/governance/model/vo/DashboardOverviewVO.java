package com.fgroupboss.ai.psm.incidentgovernance.governance.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 集团驾驶舱概览。
 */
@Data
public class DashboardOverviewVO {

    private Long tenantId;
    private long totalWorkPermits;
    private long totalAlarms;
    private long totalHazards;
    private long totalMocs;
    private long totalIncidents;
    private long totalOpenAuditIssues;
    private List<SiteDwFactSummaryVO> siteSummaries;
}
