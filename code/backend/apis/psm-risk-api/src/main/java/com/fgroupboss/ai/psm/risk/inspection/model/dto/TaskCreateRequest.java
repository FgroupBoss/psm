package com.fgroupboss.ai.psm.risk.inspection.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class TaskCreateRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private Long planId;
    @NotNull(message = "routeId is required")
    private Long routeId;
    @NotNull(message = "scheduledStart is required")
    private LocalDateTime scheduledStart;
    @NotNull(message = "scheduledEnd is required")
    private LocalDateTime scheduledEnd;
    private Long executorId;
}
