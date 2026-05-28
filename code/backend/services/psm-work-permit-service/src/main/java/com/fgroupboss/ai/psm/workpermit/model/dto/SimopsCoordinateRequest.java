package com.fgroupboss.ai.psm.workpermit.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SimopsCoordinateRequest {

    @NotNull
    private Long tenantId;
    @NotBlank
    private String decision;
    private String opinion;
    private String conditionsText;
    private String coordinatorName;
}
