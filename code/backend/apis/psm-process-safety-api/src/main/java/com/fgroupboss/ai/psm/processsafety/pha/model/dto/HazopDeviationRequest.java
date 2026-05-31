package com.fgroupboss.ai.psm.processsafety.pha.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HazopDeviationRequest {
    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private String parameter;
    private String guideword;
    private String deviationDesc;
    private String riskLevel;
}
