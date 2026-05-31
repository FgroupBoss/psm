package com.fgroupboss.ai.psm.processsafety.pha.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class PhaRecommendationRequest {
    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private Long projectId;
    private Long deviationId;
    @NotBlank(message = "recNo is required")
    private String recNo;
    @NotBlank(message = "description is required")
    private String description;
    private Long ownerUserId;
    private LocalDateTime dueAt;
}
