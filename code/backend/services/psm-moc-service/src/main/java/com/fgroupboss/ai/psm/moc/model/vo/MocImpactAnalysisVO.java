package com.fgroupboss.ai.psm.moc.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MocImpactAnalysisVO {
    private Long id;
    private Long tenantId;
    private Long changeId;
    private String discipline;
    private String impactDesc;
    private String riskLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
