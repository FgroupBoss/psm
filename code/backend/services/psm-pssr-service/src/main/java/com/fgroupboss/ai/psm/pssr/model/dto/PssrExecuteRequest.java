package com.fgroupboss.ai.psm.pssr.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PssrExecuteRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotNull(message = "checkItemId is required")
    private Long checkItemId;
    @NotBlank(message = "result is required")
    private String result;
    private String remark;
    private Long executorUserId;
}
