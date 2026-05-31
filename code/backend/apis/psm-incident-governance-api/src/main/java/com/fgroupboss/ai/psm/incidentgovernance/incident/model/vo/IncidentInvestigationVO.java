package com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncidentInvestigationVO {

    private Long id;
    private Long tenantId;
    private Long incidentId;
    private Long leadUserId;
    private String scopeDesc;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
