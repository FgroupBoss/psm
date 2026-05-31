package com.fgroupboss.ai.psm.processsafety.moc.model.dto;

import lombok.Data;

@Data
public class MocWorkflowActionRequest {
    private Long tenantId;
    private Long approverUserId;
    private String decision;
    private String commentText;
    private String taskDesc;
    private Long ownerUserId;
}
