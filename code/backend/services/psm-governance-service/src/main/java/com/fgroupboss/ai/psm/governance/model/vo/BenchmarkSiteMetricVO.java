package com.fgroupboss.ai.psm.governance.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 跨基地对标单项。
 */
@Data
public class BenchmarkSiteMetricVO {

    private Long siteId;
    private String siteCode;
    private String siteName;
    private BigDecimal metricValue;
    private BigDecimal targetValue;
    private BigDecimal gapToTarget;
    private int rank;
}
