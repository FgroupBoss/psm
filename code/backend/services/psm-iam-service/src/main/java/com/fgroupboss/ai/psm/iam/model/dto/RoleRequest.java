package com.fgroupboss.ai.psm.iam.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 角色保存请求。
 */
@Data
public class RoleRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "roleCode is required")
    private String roleCode;

    @NotBlank(message = "roleName is required")
    private String roleName;

    private String roleType;
    private String description;
    private String status;
}
