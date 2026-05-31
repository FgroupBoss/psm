package com.fgroupboss.ai.psm.identity.iam.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 租户保存请求。
 */
@Data
public class TenantRequest {

    @NotBlank(message = "tenantCode is required")
    private String tenantCode;

    @NotBlank(message = "tenantName is required")
    private String tenantName;

    private String tenantType;
    private String status;
    private Long adminUserId;
    private String dataIsolationMode;
}
