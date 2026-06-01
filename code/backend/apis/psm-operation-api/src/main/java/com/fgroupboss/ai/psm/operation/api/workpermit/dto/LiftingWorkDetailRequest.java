package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LiftingWorkDetailRequest {

    private Long tenantId;
    private String loadName;
    private BigDecimal loadWeightT;
    private Long craneId;
    private BigDecimal radiusM;
    private String liftingPoint;
    private String landingPoint;
    private String planRef;
}
