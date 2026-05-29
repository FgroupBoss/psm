package com.fgroupboss.ai.psm.governance.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 指标定义创建/更新请求。
 */
@Data
public class GovMetricDefinitionRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "metricCode is required")
    private String metricCode;

    @NotBlank(message = "metricName is required")
    private String metricName;

    @NotBlank(message = "metricDomain is required")
    private String metricDomain;

    @NotBlank(message = "statisticPeriod is required")
    private String statisticPeriod;

    private String formulaVersion;
    private BigDecimal targetValue;
    private Long ownerOrgId;
    private Integer enabledFlag;
}
