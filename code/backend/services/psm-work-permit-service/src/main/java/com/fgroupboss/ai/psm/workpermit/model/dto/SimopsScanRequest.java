package com.fgroupboss.ai.psm.workpermit.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SimopsScanRequest {

    @NotNull
    private Long tenantId;
    @NotNull
    private Long workPermitId;
    @NotBlank
    private String scanStage;
}
