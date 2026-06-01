package com.fgroupboss.ai.psm.processsafety.pssr.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class PssrProjectRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private String pssrNo;
    @NotBlank(message = "projectName is required")
    private String projectName;
    private String sourceType;
    private Long sourceBizId;
    private Long areaId;
    private Long equipmentId;
    private LocalDateTime plannedStartupAt;
    private String status;
}
