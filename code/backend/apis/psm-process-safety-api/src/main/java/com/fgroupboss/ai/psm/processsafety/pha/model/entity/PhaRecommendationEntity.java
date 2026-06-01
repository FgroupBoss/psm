package com.fgroupboss.ai.psm.processsafety.pha.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pha_recommendation")
public class PhaRecommendationEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long projectId;
    private Long deviationId;
    private String recNo;
    private String description;
    private Long ownerUserId;
    private LocalDateTime dueAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}