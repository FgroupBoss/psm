package com.fgroupboss.ai.psm.processsafety.moc.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MocChangeRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "changeNo is required")
    private String changeNo;

    @NotBlank(message = "changeTitle is required")
    private String changeTitle;

    private String changeType;
    private String changeLevel;
    private Integer temporaryFlag;
    private Integer emergencyFlag;
    private Long affectedAreaId;
    private Long affectedEquipmentId;
    private String riskLevel;
}
