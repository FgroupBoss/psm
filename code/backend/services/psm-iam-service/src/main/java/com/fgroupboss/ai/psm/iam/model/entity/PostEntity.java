package com.fgroupboss.ai.psm.iam.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 岗位实体。
 */
@Data
@TableName("sys_post")
public class PostEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String postCode;
    private String postName;
    private Long orgId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
