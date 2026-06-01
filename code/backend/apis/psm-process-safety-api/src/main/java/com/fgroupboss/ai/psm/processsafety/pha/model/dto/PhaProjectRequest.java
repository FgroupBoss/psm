package com.fgroupboss.ai.psm.processsafety.pha.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class PhaProjectRequest {
    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "projectNo is required")
    private String projectNo;
    @NotBlank(message = "projectName is required")
    private String projectName;
    private Long siteId;
    private Long unitId;
    private Long majorHazardId;
    private String method;
    private String version;
    private LocalDateTime reviewDueAt;
    private String status;
}
