package com.fgroupboss.ai.psm.operation.api.contractor.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContractorEligibilityResult {

    private boolean passed;
    private List<ContractorEligibilityReason> reasons;
}
