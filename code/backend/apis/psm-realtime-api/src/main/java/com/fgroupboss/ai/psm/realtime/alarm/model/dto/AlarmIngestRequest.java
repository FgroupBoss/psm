package com.fgroupboss.ai.psm.realtime.alarm.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class AlarmIngestRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "sourceType is required")
    private String sourceType;

    private String sourceCode;

    @NotBlank(message = "title is required")
    private String title;

    private String content;

    @NotBlank(message = "alarmLevel is required")
    private String alarmLevel;

    private Long areaId;
    private Long unitId;
    private Long equipmentId;
    private Long monitorPointId;
    private Long hazardId;
    private String rawValue;
    /** 发生时间；为空则取当前时间 */
    private Date occurredAt;
}
