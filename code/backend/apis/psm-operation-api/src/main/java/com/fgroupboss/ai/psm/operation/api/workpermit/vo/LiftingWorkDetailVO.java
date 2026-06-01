package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LiftingWorkDetailVO {

    private Long id;
    private Long workPermitId;
    private String loadName;
    private BigDecimal loadWeightT;
    private String liftingLevel;
    private Long craneId;
    private BigDecimal radiusM;
    private String liftingPoint;
    private String landingPoint;
    private String planRef;
    private String ruleVersion;
    private Date validUntil;
}
