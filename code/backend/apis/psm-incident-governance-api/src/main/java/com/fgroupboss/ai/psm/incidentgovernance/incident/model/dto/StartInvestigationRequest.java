package com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StartInvestigationRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "leadUserId is required")
    private Long leadUserId;

    private String scopeDesc;
}
