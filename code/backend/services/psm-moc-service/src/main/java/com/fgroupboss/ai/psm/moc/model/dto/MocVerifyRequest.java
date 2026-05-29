package com.fgroupboss.ai.psm.moc.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MocVerifyRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    private Boolean passed;
    private String remark;
}
