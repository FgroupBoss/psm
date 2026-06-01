package com.fgroupboss.ai.psm.incidentgovernance.governance.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 指标定义视图。
 */
@Data
public class GovMetricDefinitionVO {

    private Long id;
    private Long tenantId;
    private String metricCode;
    private String metricName;
    private String metricDomain;
    private String statisticPeriod;
    private String formulaVersion;
    private BigDecimal targetValue;
    private Long ownerOrgId;
    private Integer enabledFlag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
