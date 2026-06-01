package com.fgroupboss.ai.psm.identity.iam.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色功能权限分配请求。
 */
@Data
public class AssignRolePermissionRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private List<Long> resourceIds;
}
