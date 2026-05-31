package com.fgroupboss.ai.psm.operation.contractor.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CompanyApproveRequest {

    @NotNull(message = "passed is required")
    private Boolean passed;
    private String opinion;
}
