package com.fgroupboss.ai.psm.iam.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色权限关系实体。
 */
@Data
@TableName("sys_role_permission")
public class RolePermissionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long roleId;
    private Long resourceId;
    private String permissionCode;
    private LocalDateTime createdAt;
}
