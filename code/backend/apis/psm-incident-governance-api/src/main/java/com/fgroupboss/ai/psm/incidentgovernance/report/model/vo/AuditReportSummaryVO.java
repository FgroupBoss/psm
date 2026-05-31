package com.fgroupboss.ai.psm.incidentgovernance.report.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class AuditReportSummaryVO {

    private long totalLogCount;
    private long criticalActionLogCount;
    private long configChangeCount;
    private long permissionChangeCount;
    private long criticalOperationBaseline;
    /** 关键操作审计覆盖率 = criticalActionLogCount / criticalOperationBaseline */
    private double criticalAuditCoverageRate;
    private Map<String, Long> bizTypeCounts = new LinkedHashMap<String, Long>();
    private Date dataRefreshedAt;
    private String dataSource;
}
