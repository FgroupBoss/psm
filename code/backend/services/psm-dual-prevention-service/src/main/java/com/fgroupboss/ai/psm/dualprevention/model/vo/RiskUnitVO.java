package com.fgroupboss.ai.psm.dualprevention.model.vo;

import lombok.Data;

@Data
public class RiskUnitVO {

    private Long id;
    private Long tenantId;
    private Long parentId;
    private String unitCode;
    private String unitName;
    private Long areaId;
    private Long equipmentId;
    private Long majorHazardId;
    private String inherentRiskLevel;
    private String residualRiskLevel;
    private Long ownerOrgId;
    private Long ownerUserId;
    private String status;
}
