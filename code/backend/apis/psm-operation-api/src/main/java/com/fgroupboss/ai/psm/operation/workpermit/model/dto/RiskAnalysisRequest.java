package com.fgroupboss.ai.psm.operation.workpermit.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RiskAnalysisRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private String hazardDesc;
    private String controlMeasure;
    private String riskLevel;
    private String analystName;
}
