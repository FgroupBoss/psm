package com.fgroupboss.ai.psm.processsafety.moc.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MocCloseConditionVO {
    private Long id;
    private Long tenantId;
    private Long changeId;
    private String conditionType;
    private Integer requiredFlag;
    private Long ownerUserId;
    private LocalDateTime dueAt;
    private LocalDateTime completedAt;
    private Long evidenceFileId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
