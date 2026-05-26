package com.fgroupboss.ai.psm.contractor.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class WorkerViolationRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private Long companyId;
    @NotNull(message = "violationTime is required")
    private LocalDateTime violationTime;
    @NotBlank(message = "violationDesc is required")
    private String violationDesc;
    private String severity;
    private String rectificationStatus;
}
