package com.fgroupboss.ai.psm.location.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LocGeofenceRuleRequest {

    @NotBlank(message = "ruleType is required")
    private String ruleType;

    private Integer thresholdValue;
    private Boolean enabled;
}
