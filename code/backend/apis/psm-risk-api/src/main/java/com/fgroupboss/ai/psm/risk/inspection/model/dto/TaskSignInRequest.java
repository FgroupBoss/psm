package com.fgroupboss.ai.psm.risk.inspection.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TaskSignInRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotNull(message = "routePointId is required")
    private Long routePointId;
    @NotBlank(message = "signType is required")
    private String signType;
    private String signCode;
    private Long operatorId;
    private String operatorName;
}
