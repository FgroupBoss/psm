package com.fgroupboss.ai.psm.incident.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncidentInterviewVO {

    private Long id;
    private Long tenantId;
    private Long incidentId;
    private String interviewee;
    private String summary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
