package com.fgroupboss.ai.psm.iam.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * IAM 用户资料视图，不返回认证密钥。
 */
@Data
public class IamUserVO {

    private Long id;
    private Long tenantId;
    private Long authUserId;
    private String username;
    private String displayName;
    private String mobile;
    private String email;
    private Long orgId;
    private Long postId;
    private String accountType;
    private String status;
    private Long permissionVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
