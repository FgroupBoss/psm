package com.fgroupboss.ai.psm.risk.dualprevention.model.vo;

import lombok.Data;

@Data
public class RiskEventVO {

    private Long id;
    private Long tenantId;
    private Long riskUnitId;
    private String eventCode;
    private String eventName;
    private String hazardFactors;
    private String possibleConsequence;
    private String inherentRiskLevel;
    private String status;
}
