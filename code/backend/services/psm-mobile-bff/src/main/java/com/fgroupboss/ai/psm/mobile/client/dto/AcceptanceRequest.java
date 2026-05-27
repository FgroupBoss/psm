package com.fgroupboss.ai.psm.mobile.client.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AcceptanceRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "acceptanceResult is required")
    private String acceptanceResult;
    private String opinion;
    private String signatureText;
}
