package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HeightWorkDetailRequest {

    private Long tenantId;
    private BigDecimal workHeightM;
    private String fallDatumDescription;
    private String workLocation;
    private String workMethod;
    private Boolean manualUpgradeFlag;
    private String manualUpgradeReason;
    private String rescuePlanRef;
    private String rescueContact;
    private Boolean communicationConfirmed;
}
