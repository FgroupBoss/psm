package com.fgroupboss.ai.psm.incident.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncidentEvidenceVO {

    private Long id;
    private Long tenantId;
    private Long incidentId;
    private String evidenceType;
    private Long fileId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
