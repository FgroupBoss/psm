package com.fgroupboss.ai.psm.moc.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MocApprovalRecordVO {
    private Long id;
    private Long tenantId;
    private Long changeId;
    private Long approverUserId;
    private String decision;
    private String commentText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
