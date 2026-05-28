package com.fgroupboss.ai.psm.majorhazard.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HazardInspectionPlanBindRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "inspectionPlanId is required")
    private Long inspectionPlanId;
}
