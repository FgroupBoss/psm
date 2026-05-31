package com.fgroupboss.ai.psm.incidentgovernance.governance.model.vo;

import lombok.Data;

/**
 * 单基地数据仓库事实汇总。
 */
@Data
public class SiteDwFactSummaryVO {

    private Long siteId;
    private String siteCode;
    private String siteName;
    private long workPermitCount;
    private long alarmCount;
    private long hazardCount;
    private long mocCount;
    private long incidentCount;
    private long openAuditIssueCount;
}
