package com.fgroupboss.ai.psm.risk.inspection.model.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class InspectionRouteRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "routeCode is required")
    private String routeCode;
    @NotBlank(message = "routeName is required")
    private String routeName;
    private Long areaId;
    private Integer estimatedMinutes;
    private String remark;
    @Valid
    private List<RoutePointRequest> points;
}
