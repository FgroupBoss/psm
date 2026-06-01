package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

@Data
public class RoadBreakTrafficPlanRequest {

    private Long tenantId;
    private String detourGeoJson;
    private String emergencyLaneGeoJson;
    private String planRef;
    private Boolean confirmed;
}
