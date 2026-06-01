package com.fgroupboss.ai.psm.risk.dualprevention.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RiskUnitRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    private Long parentId;

    @NotBlank(message = "unitCode is required")
    private String unitCode;

    @NotBlank(message = "unitName is required")
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
