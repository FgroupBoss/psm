package com.fgroupboss.ai.psm.pha.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pha_project")
public class PhaProjectEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String projectNo;
    private String projectName;
    private Long siteId;
    private Long unitId;
    private Long majorHazardId;
    private String method;
    private String version;
    private LocalDateTime reviewDueAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}