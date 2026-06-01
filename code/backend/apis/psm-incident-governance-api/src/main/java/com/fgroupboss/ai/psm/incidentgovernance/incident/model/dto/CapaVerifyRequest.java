package com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CapaVerifyRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "passed is required")
    private Boolean passed;

    private Long evidenceFileId;
    private String remark;
}
