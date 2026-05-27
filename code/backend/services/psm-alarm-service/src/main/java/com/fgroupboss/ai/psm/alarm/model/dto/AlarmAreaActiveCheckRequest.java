package com.fgroupboss.ai.psm.alarm.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AlarmAreaActiveCheckRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "areaId is required")
    private Long areaId;

    /** 最低阻断等级，默认 LEVEL_2（含 LEVEL_1、LEVEL_2）。 */
    private String minLevel;
}
