package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SitePermitRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private String signatureText;
    private String locationText;
    private String remark;
}

