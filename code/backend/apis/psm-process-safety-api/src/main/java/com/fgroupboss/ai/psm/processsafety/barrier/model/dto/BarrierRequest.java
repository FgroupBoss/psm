package com.fgroupboss.ai.psm.processsafety.barrier.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class BarrierRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "barrierCode is required")
    private String barrierCode;
    @NotBlank(message = "barrierName is required")
    private String barrierName;
    private String barrierType;
    private Long majorHazardId;
    private Long hazopScenarioId;
    private Long ownerOrgId;
    private BigDecimal healthScore;
    private String status;
}
