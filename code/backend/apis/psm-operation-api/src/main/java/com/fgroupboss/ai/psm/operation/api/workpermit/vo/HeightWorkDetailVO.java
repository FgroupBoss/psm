package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class HeightWorkDetailVO {

    private Long id;
    private Long workPermitId;
    private BigDecimal workHeightM;
    private String fallDatumDescription;
    private String workLocation;
    private String workMethod;
    private String heightLevel;
    private String riskClass;
    private Boolean manualUpgradeFlag;
    private String manualUpgradeReason;
    private String rescuePlanRef;
    private String rescueContact;
    private Boolean communicationConfirmed;
    private String ruleVersion;
    private Date validUntil;
}
