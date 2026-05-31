package com.fgroupboss.ai.psm.identity.iam.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * IAM 用户资料保存请求。
 */
@Data
public class IamUserRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private Long authUserId;

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "displayName is required")
    private String displayName;

    private String mobile;
    private String email;
    private Long orgId;
    private Long postId;
    private String accountType;
    private String status;
}
