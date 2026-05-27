package com.fgroupboss.ai.psm.report.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class MajorHazardReportSummaryVO {

    private long totalCount;
    private long publishedCount;
    private long completeArchiveCount;
    /** 重大危险源档案完整率 = completeArchiveCount / totalCount */
    private double archiveCompletenessRate;
    private Map<String, Long> levelCounts = new LinkedHashMap<String, Long>();
    private Map<String, Long> statusCounts = new LinkedHashMap<String, Long>();
    private Date dataRefreshedAt;
    private String dataSource;
}
