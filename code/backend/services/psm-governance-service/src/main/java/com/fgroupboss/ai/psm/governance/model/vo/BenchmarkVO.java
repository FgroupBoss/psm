package com.fgroupboss.ai.psm.governance.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 跨基地指标对标结果。
 */
@Data
public class BenchmarkVO {

    private Long tenantId;
    private String metricCode;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private List<BenchmarkSiteMetricVO> siteMetrics;
}
