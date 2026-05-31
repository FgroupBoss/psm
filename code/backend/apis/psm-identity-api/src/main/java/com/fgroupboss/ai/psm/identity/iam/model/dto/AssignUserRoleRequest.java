package com.fgroupboss.ai.psm.identity.iam.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户角色分配请求。
 */
@Data
public class AssignUserRoleRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private List<Long> roleIds;
}
