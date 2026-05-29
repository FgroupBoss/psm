package com.fgroupboss.ai.psm.incident.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class IncidentTimelineRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "eventAt is required")
    private LocalDateTime eventAt;

    @NotBlank(message = "eventDesc is required")
    private String eventDesc;
}
