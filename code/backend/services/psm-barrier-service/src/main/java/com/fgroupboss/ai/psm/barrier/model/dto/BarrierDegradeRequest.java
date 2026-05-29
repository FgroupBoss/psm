package com.fgroupboss.ai.psm.barrier.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BarrierDegradeRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private String reason;
}
