package com.fgroupboss.ai.psm.operation.contractor.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CompanyReasonRequest {

    @NotBlank(message = "reason is required")
    private String reason;
}
