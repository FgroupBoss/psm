package com.fgroupboss.ai.psm.barrier.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class MiDefectRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private String defectNo;
    @NotNull(message = "equipmentId is required")
    private Long equipmentId;
    @NotBlank(message = "defectLevel is required")
    private String defectLevel;
    private String sourceType;
    @NotBlank(message = "description is required")
    private String description;
    private LocalDateTime repairDeadline;
}
