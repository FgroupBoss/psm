package com.fgroupboss.ai.psm.pha.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pha_node")
public class PhaNodeEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long projectId;
    private String nodeNo;
    private String nodeName;
    private String designIntent;
    private String parameters;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}