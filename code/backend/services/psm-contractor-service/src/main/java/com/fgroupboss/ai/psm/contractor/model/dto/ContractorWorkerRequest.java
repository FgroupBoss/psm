package com.fgroupboss.ai.psm.contractor.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ContractorWorkerRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotNull(message = "companyId is required")
    private Long companyId;
    @NotBlank(message = "workerCode is required")
    private String workerCode;
    @NotBlank(message = "name is required")
    private String name;
    private String phoneMasked;
    private String tradeType;
    private String gateCardNo;
    private String locationTagNo;
}
