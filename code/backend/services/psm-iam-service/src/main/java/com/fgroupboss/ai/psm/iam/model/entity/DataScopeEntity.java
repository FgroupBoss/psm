package com.fgroupboss.ai.psm.iam.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色数据权限实体，范围 ID 使用 JSON 字符串保存。
 */
@Data
@TableName("sys_data_scope")
public class DataScopeEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long roleId;
    private String scopeType;
    private String orgIds;
    private String areaIds;
    private String unitIds;
    private Boolean includeChildren;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
