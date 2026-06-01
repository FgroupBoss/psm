package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RoadBreakDetailRequest {

    private Long tenantId;
    private String roadId;
    private String areaGeoJson;
    private String reason;
    private Date startAt;
    private Date endAt;
    private String responsibleUnit;
}
