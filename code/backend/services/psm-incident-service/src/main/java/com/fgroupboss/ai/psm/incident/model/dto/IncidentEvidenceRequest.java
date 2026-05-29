package com.fgroupboss.ai.psm.incident.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class IncidentEvidenceRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "evidenceType is required")
    private String evidenceType;

    private Long fileId;
    private String description;
}
