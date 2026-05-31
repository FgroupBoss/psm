package com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncidentCapaVO {

    private Long id;
    private Long tenantId;
    private Long incidentId;
    private String capaNo;
    private String capaType;
    private Long ownerUserId;
    private LocalDateTime dueAt;
    private Long verificationUserId;
    private Long evidenceFileId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
