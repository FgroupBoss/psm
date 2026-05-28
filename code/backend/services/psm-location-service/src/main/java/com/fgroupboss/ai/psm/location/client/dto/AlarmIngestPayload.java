package com.fgroupboss.ai.psm.location.client.dto;

import lombok.Data;

import java.util.Date;

/**
 * 与 psm-alarm-service {@code AlarmIngestRequest} 字段对齐。
 */
@Data
public class AlarmIngestPayload {

    private Long tenantId;
    private String sourceType;
    private String sourceCode;
    private String title;
    private String content;
    private String alarmLevel;
    private Long areaId;
    private Date occurredAt;
}
