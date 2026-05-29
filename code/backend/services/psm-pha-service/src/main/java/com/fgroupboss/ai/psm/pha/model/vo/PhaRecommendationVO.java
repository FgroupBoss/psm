package com.fgroupboss.ai.psm.pha.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PhaRecommendationVO {
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
}