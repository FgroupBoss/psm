package com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncidentTimelineVO {

    private Long id;
    private Long tenantId;
    private Long incidentId;
    private LocalDateTime eventAt;
    private String eventDesc;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
