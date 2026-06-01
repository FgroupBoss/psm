package com.fgroupboss.ai.psm.realtime.location.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LocTagBindRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "personType is required")
    private String personType;

    @NotNull(message = "personId is required")
    private Long personId;

    private Long contractorId;
}
