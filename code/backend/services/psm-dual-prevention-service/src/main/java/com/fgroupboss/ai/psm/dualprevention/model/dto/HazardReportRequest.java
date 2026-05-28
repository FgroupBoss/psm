package com.fgroupboss.ai.psm.dualprevention.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class HazardReportRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "hazardLevel is required")
    private String hazardLevel;

    @NotBlank(message = "sourceType is required")
    private String sourceType;

    private Long sourceBizId;
    private Long riskUnitId;
    private Long areaId;

    @NotBlank(message = "description is required")
    private String description;

    private LocalDateTime foundAt;
    private LocalDateTime rectificationDeadline;
    private Long contractorId;
}
