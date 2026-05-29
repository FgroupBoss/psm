package com.fgroupboss.ai.psm.governance.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 指标快照视图。
 */
@Data
public class GovMetricSnapshotVO {

    private Long id;
    private Long tenantId;
    private String metricCode;
    private Long siteId;
    private String siteName;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private BigDecimal metricValue;
    private BigDecimal targetValue;
    private LocalDateTime calculationTime;
    private String inputHash;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
