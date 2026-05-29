package com.fgroupboss.ai.psm.moc.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MocImplementationTaskVO {
    private Long id;
    private Long tenantId;
    private Long changeId;
    private String taskDesc;
    private Long ownerUserId;
    private LocalDateTime plannedAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
