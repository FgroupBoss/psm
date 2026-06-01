package com.fgroupboss.ai.psm.identity.iam.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜单、按钮和接口权限资源实体。
 */
@Data
@TableName("sys_menu")
public class MenuResourceEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long parentId;
    private String resourceType;
    private String resourceCode;
    private String resourceName;
    private String routePath;
    private String apiPath;
    private String httpMethod;
    private Integer sortOrder;
    private Boolean visible;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
