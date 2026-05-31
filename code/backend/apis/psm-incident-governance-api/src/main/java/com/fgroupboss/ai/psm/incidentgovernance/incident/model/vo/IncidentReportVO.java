package com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncidentReportVO {

    private Long id;
    private Long tenantId;
    private String incidentNo;
    private String incidentType;
    private String incidentLevel;
    private LocalDateTime occurredAt;
    private Long areaId;
    private Long equipmentId;
    private String sourceType;
    private Long sourceBizId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
