package com.fgroupboss.ai.psm.inspection.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TaskStartRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private Long executorId;
}
