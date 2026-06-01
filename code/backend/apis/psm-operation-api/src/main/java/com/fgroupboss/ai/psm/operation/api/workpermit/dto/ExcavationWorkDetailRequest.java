package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExcavationWorkDetailRequest {

    private Long tenantId;
    private String areaGeoJson;
    private BigDecimal depthM;
    private BigDecimal areaM2;
    private String method;
    private String drawingRef;
}
