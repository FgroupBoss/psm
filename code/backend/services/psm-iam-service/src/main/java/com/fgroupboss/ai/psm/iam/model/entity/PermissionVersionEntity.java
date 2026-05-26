package com.fgroupboss.ai.psm.iam.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限版本实体。
 */
@Data
@TableName("sys_permission_version")
public class PermissionVersionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long userId;
    private Long versionNo;
    private String changedReason;
    private LocalDateTime updatedAt;
}
