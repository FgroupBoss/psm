package com.fgroupboss.ai.psm.risk.dualprevention.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HazardRectifyRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    private String content;
    private String evidenceFileIds;
}
