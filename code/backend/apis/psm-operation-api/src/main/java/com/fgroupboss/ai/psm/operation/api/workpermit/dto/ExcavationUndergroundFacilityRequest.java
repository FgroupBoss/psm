package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExcavationUndergroundFacilityRequest {

    private Long tenantId;
    private String facilityType;
    private String ownerUnit;
    private String position;
    private BigDecimal depthM;
    private String detectionMethod;
    private Boolean confirmed;
}
