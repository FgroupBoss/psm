package com.fgroupboss.ai.psm.identity.audit.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AuditLogIngestRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private String operatorName;
    @NotBlank(message = "action is required")
    private String action;
    @NotBlank(message = "bizType is required")
    private String bizType;
    private Long bizId;
    private String beforeValue;
    private String afterValue;
    private String result;
}
