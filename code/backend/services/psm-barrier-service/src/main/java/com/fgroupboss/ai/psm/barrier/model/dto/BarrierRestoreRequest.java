package com.fgroupboss.ai.psm.barrier.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class BarrierRestoreRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private BigDecimal healthScore;
    private String remark;
}
