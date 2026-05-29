package com.fgroupboss.ai.psm.incident.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncidentRootCauseVO {

    private Long id;
    private Long tenantId;
    private Long incidentId;
    private String causeType;
    private String causeDesc;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
