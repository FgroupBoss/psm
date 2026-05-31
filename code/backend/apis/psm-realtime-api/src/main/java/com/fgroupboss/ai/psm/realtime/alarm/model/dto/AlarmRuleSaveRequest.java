package com.fgroupboss.ai.psm.realtime.alarm.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AlarmRuleSaveRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "ruleCode is required")
    private String ruleCode;

    @NotBlank(message = "ruleName is required")
    private String ruleName;

    @NotBlank(message = "ruleType is required")
    private String ruleType;

    private String configJson;

    private String status;
}
