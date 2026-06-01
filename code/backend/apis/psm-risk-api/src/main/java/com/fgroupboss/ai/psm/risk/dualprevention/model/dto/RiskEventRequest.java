package com.fgroupboss.ai.psm.risk.dualprevention.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RiskEventRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "eventCode is required")
    private String eventCode;

    @NotBlank(message = "eventName is required")
    private String eventName;

    private String hazardFactors;
    private String possibleConsequence;
    private String inherentRiskLevel;
    private String status;
}
