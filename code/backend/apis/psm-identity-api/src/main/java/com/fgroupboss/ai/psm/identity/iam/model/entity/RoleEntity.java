package com.fgroupboss.ai.psm.identity.iam.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体。
 */
@Data
@TableName("sys_role")
public class RoleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String roleCode;
    private String roleName;
    private String roleType;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
