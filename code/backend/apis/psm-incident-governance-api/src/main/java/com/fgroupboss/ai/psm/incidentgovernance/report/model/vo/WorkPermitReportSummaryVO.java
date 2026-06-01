package com.fgroupboss.ai.psm.incidentgovernance.report.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class WorkPermitReportSummaryVO {

    private long totalCount;
    private long requiringSitePermitCount;
    private long tracedPermitCount;
    private long contractorInvolvedCount;
    private long contractorCheckedCount;
    private long suspendedCount;
    private long overdueCount;
    /** 特殊作业系统办理率 = systemPermitCount / (systemPermitCount + offlineWorkCount) */
    private double systemProcessingRate;
    /** 现场许可留痕率 = tracedPermitCount / requiringSitePermitCount */
    private double siteTraceRate;
    /** 承包商资质校验覆盖率 = contractorCheckedCount / contractorInvolvedCount */
    private double contractorEligibilityCoverageRate;
    private Map<String, Long> statusCounts = new LinkedHashMap<String, Long>();
    private Map<String, Long> workTypeCounts = new LinkedHashMap<String, Long>();
    private Date dataRefreshedAt;
    private String dataSource;
}
