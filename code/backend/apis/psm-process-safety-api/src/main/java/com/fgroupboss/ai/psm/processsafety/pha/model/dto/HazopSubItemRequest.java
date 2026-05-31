package com.fgroupboss.ai.psm.processsafety.pha.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HazopSubItemRequest {
    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private String causeDesc;
    private String frequency;
    private String consequenceDesc;
    private String severity;
    private String safeguardType;
    private String safeguardDesc;
    private String effectiveness;
}
