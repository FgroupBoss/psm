package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class RoadBreakDetailVO {

    private Long id;
    private Long workPermitId;
    private String roadId;
    private String areaGeoJson;
    private String reason;
    private Date startAt;
    private Date endAt;
    private String responsibleUnit;
    private String ruleVersion;
}
