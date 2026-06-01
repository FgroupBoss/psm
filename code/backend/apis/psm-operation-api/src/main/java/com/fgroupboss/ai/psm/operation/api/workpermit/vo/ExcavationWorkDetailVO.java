package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ExcavationWorkDetailVO {

    private Long id;
    private Long workPermitId;
    private String areaGeoJson;
    private BigDecimal depthM;
    private BigDecimal areaM2;
    private String method;
    private String drawingRef;
    private String ruleVersion;
    private Date validUntil;
}
