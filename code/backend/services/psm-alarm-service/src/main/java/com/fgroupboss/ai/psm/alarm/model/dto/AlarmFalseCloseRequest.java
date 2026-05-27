package com.fgroupboss.ai.psm.alarm.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AlarmFalseCloseRequest {

    @NotBlank(message = "reason is required")
    private String reason;
}
