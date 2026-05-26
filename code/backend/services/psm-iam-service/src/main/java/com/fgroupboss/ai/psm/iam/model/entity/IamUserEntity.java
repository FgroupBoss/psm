package com.fgroupboss.ai.psm.iam.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * IAM 用户资料实体，不承载认证密钥。
 */
@Data
@TableName("sys_user")
public class IamUserEntity {

    @TableId(type = IdType.AUTO)
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
    private Boolean deleted;
}
