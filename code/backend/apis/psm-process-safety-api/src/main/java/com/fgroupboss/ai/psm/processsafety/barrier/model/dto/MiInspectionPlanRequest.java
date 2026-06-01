package com.fgroupboss.ai.psm.processsafety.barrier.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class MiInspectionPlanRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotNull(message = "equipmentId is required")
    private Long equipmentId;
    @NotBlank(message = "planName is required")
    private String planName;
    private Integer cycleDays;
    private LocalDateTime nextDueAt;
    private String status;
}
