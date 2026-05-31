package com.fgroupboss.ai.psm.processsafety.moc.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MocImpactAnalysisRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    private String discipline;
    private String impactDesc;
    private String riskLevel;
}
