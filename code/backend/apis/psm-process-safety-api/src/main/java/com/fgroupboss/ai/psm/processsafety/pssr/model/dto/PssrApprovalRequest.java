package com.fgroupboss.ai.psm.processsafety.pssr.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PssrApprovalRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotNull(message = "approverUserId is required")
    private Long approverUserId;
    private String commentText;
}
