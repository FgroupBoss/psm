package com.fgroupboss.ai.psm.risk.dualprevention.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HazardReviewRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "passed is required")
    private Boolean passed;

    private String content;
}
