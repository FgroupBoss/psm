package com.fgroupboss.ai.psm.processsafety.moc.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MocApproveRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    private Long approverUserId;
    private String decision;
    private String commentText;
    private Boolean passed;
}
