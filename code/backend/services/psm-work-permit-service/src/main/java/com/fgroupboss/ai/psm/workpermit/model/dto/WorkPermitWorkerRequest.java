package com.fgroupboss.ai.psm.workpermit.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class WorkPermitWorkerRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "workerType is required")
    private String workerType;
    private Long workerId;
    @NotBlank(message = "workerName is required")
    private String workerName;
    private String roleCode;
    private Long companyId;
}
