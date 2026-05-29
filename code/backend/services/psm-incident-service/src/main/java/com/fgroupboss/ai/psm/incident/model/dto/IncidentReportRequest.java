package com.fgroupboss.ai.psm.incident.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class IncidentReportRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "incidentType is required")
    private String incidentType;

    @NotBlank(message = "incidentLevel is required")
    private String incidentLevel;

    private LocalDateTime occurredAt;
    private Long areaId;
    private Long equipmentId;
    private String sourceType;
    private Long sourceBizId;
}
