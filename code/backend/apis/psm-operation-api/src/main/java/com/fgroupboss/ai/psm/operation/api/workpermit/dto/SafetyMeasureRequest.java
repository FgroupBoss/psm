package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SafetyMeasureRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private String confirmStatus;
    private String remark;
    private String attachmentRef;
}

