package com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class IncidentCapaRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "capaType is required")
    private String capaType;

    @NotNull(message = "ownerUserId is required")
    private Long ownerUserId;

    private LocalDateTime dueAt;
    private Long verificationUserId;
}
