package com.fgroupboss.ai.psm.realtime.alarm.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AlarmToHazardRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    /** 覆盖默认隐患描述（默认取报警标题+内容）。 */
    private String description;

    /** 隐患等级，默认 MAJOR。 */
    private String hazardLevel;
}
