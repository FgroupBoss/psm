package com.fgroupboss.ai.psm.risk.inspection.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OverdueScanRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
}
