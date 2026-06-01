package com.fgroupboss.ai.psm.operation.contractor.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ContractorCompanyRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "companyCode is required")
    private String companyCode;
    @NotBlank(message = "companyName is required")
    private String companyName;
    private String contactName;
    private String contactPhone;
    private String businessScope;
    private String remark;
}
