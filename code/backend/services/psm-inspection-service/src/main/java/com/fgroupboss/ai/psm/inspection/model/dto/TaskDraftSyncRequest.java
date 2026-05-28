package com.fgroupboss.ai.psm.inspection.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TaskDraftSyncRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "clientDraftId is required")
    private String clientDraftId;
    private Long taskId;
    private String payloadJson;
}
