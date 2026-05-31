package com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class IncidentRootCauseRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "causeType is required")
    private String causeType;

    @NotBlank(message = "causeDesc is required")
    private String causeDesc;
}
