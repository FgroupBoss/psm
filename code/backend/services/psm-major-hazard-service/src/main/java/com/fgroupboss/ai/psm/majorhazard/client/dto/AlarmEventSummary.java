package com.fgroupboss.ai.psm.majorhazard.client.dto;

import lombok.Data;

import java.util.Date;

/** 报警服务返回的事件摘要（与 alarm-service AlarmEventVO 字段对齐）。 */
@Data
public class AlarmEventSummary {

    private Long id;
    private Long tenantId;
    private String alarmNo;
    private String sourceType;
    private String sourceCode;
    private String title;
    private String content;
    private String alarmLevel;
    private String status;
    private Long areaId;
    private Long hazardId;
    private Integer occurrenceCount;
    private Date firstOccurredAt;
    private Date lastOccurredAt;
}
