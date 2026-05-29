package com.fgroupboss.ai.psm.pha.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LopaScenarioVO {
    private Long id;
    private Long tenantId;
    private String scenarioNo;
    private Long projectId;
    private Long deviationId;
    private java.math.BigDecimal initiatingEventFrequency;
    private String consequenceSeverity;
    private java.math.BigDecimal targetFrequency;
    private java.math.BigDecimal mitigatedFrequency;
    private String silRecommendation;
    private String calculationVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}