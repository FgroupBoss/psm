package com.fgroupboss.ai.psm.mobile.client.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MeasureConfirmRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotNull(message = "measureId is required")
    private Long measureId;
    private String confirmStatus;
    private String remark;
    private String attachmentRef;
}
