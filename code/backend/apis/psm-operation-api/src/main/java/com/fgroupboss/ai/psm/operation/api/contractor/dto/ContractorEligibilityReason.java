package com.fgroupboss.ai.psm.operation.api.contractor.dto;

import lombok.Data;

@Data
public class ContractorEligibilityReason {

    private String code;
    private String message;
    private Long workerId;
}
