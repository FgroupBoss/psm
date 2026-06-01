package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ExcavationUndergroundFacilityVO {

    private Long id;
    private String facilityType;
    private String ownerUnit;
    private String position;
    private BigDecimal depthM;
    private String detectionMethod;
    private Boolean confirmed;
    private Date createdAt;
}
