package com.fgroupboss.ai.psm.location.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContractorEligibilityResult {

    private boolean passed;
    private List<ContractorEligibilityReason> reasons;
}
