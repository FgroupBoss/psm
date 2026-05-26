package com.fgroupboss.ai.psm.contractor.model.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EligibilityCheckRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotNull(message = "companyId is required")
    private Long companyId;
    @NotEmpty(message = "workerIds is required")
    private List<Long> workerIds;
    private String workType;
    private String checkPoint;
}
