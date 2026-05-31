package com.fgroupboss.ai.psm.risk.inspection.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class InspectionPlanRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "planCode is required")
    private String planCode;
    @NotBlank(message = "planName is required")
    private String planName;
    @NotNull(message = "routeId is required")
    private Long routeId;
    @NotBlank(message = "cycleType is required")
    private String cycleType;
    private String cronExpr;
    private Long teamId;
    private String teamName;
    private Long defaultExecutorId;
    private Long majorHazardId;
    private Boolean enabled;
}
