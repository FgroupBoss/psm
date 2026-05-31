package com.fgroupboss.ai.psm.risk.majorhazard.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class HazardStatusRequest {

    @NotBlank(message = "targetStatus is required")
    private String targetStatus;
    private String reason;
}
