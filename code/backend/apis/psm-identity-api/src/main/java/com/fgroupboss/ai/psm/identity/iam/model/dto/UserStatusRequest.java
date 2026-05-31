package com.fgroupboss.ai.psm.identity.iam.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户状态变更请求。
 */
@Data
public class UserStatusRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "status is required")
    private String status;
}
