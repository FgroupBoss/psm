package com.fgroupboss.ai.psm.risk.dualprevention.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ControlMeasureRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "measureType is required")
    private String measureType;

    @NotBlank(message = "measureContent is required")
    private String measureContent;

    private String responsiblePost;
    private Integer checkCycleDays;
    private String status;
}
