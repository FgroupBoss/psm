package com.fgroupboss.ai.psm.realtime.location.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class LocGeofenceRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "fenceCode is required")
    private String fenceCode;

    @NotBlank(message = "fenceName is required")
    private String fenceName;

    private Long areaId;

    @NotBlank(message = "fenceType is required")
    private String fenceType;

    private String geometryJson;
    private String status;
    private List<LocGeofenceRuleRequest> rules;
}
