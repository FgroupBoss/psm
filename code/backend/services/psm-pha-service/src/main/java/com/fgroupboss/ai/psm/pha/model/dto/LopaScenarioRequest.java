package com.fgroupboss.ai.psm.pha.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class LopaScenarioRequest {
    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "scenarioNo is required")
    private String scenarioNo;
    private Long projectId;
    private Long deviationId;
    private BigDecimal initiatingEventFrequency;
    private String consequenceSeverity;
    private BigDecimal targetFrequency;
    private String silRecommendation;
    private String calculationVersion;
}
