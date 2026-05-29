package com.fgroupboss.ai.psm.pssr.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PssrIssueActionRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private String content;
    private Boolean passed;
}
