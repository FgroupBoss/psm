package com.fgroupboss.ai.psm.processsafety.pha.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PhaReviewPlanVO {
    private Long id;
    private Long tenantId;
    private Long projectId;
    private LocalDateTime planAt;
    private Long reviewerUserId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}