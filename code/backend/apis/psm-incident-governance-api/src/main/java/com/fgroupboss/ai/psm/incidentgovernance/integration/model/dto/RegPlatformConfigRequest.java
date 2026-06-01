package com.fgroupboss.ai.psm.incidentgovernance.integration.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RegPlatformConfigRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "platformCode is required")
    private String platformCode;

    @NotBlank(message = "platformName is required")
    private String platformName;

    @NotBlank(message = "baseUrl is required")
    private String baseUrl;

    private String authType;

    private String credentialRef;

    private Integer enabled;

    private String remark;
}
