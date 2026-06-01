package com.fgroupboss.ai.psm.incidentgovernance.report.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ContractorReportSummaryVO {

    private long companyCount;
    private long approvedCompanyCount;
    private long workerCount;
    private long certificateExpiredCount;
    private long trainingInvalidCount;
    private long blacklistCount;
    private double trainingQualifiedRate;
    private Map<String, Long> companyStatusCounts = new LinkedHashMap<String, Long>();
    private Date dataRefreshedAt;
    private String dataSource;
}
