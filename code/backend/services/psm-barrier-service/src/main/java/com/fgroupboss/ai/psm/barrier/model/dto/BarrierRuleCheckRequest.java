package com.fgroupboss.ai.psm.barrier.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BarrierRuleCheckRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private Long areaId;
    private Long majorHazardId;
    private List<Long> barrierIds;
    private String scene;
}
