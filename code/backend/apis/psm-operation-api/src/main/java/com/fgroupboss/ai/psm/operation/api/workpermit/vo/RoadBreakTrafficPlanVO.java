package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class RoadBreakTrafficPlanVO {

    private Long id;
    private Long workPermitId;
    private String detourGeoJson;
    private String emergencyLaneGeoJson;
    private String planRef;
    private Boolean confirmed;
    private Date updatedAt;
}
