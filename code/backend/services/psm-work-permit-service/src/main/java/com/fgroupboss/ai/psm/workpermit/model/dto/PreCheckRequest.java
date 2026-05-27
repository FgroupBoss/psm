package com.fgroupboss.ai.psm.workpermit.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PreCheckRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "checkPoint is required")
    private String checkPoint;
}
