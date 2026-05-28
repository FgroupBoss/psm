package com.fgroupboss.ai.psm.integration.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class RegReportTriggerRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "platformCode is required")
    private String platformCode;

    @NotBlank(message = "dataDomain is required")
    private String dataDomain;

    private String triggerType;

    private LocalDateTime dataWindowStart;

    private LocalDateTime dataWindowEnd;
}
