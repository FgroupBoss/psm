package com.fgroupboss.ai.psm.operation.api.contractor.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContractorEligibilityRequest {

    private Long tenantId;
    private Long companyId;
    private List<Long> workerIds;
    private String workType;
    private String checkPoint;
}
