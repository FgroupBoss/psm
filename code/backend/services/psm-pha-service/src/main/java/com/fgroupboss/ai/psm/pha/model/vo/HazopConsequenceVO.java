package com.fgroupboss.ai.psm.pha.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HazopConsequenceVO {
    private Long id;
    private Long tenantId;
    private Long deviationId;
    private String consequenceDesc;
    private String severity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}