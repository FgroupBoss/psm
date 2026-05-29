package com.fgroupboss.ai.psm.moc.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MocTrainingRequirementVO {
    private Long id;
    private Long tenantId;
    private Long changeId;
    private String trainingDesc;
    private Long ownerUserId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
