package com.fgroupboss.ai.psm.pssr.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PssrTemplateRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "templateCode is required")
    private String templateCode;
    @NotBlank(message = "templateName is required")
    private String templateName;
    private String unitType;
    private String status;
}
