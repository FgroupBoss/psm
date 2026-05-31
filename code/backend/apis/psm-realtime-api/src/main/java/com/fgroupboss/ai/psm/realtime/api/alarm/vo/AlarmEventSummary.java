package com.fgroupboss.ai.psm.realtime.api.alarm.vo;

import lombok.Data;

import java.util.Date;

/**
 * 报警事件轻量摘要，用于风险域等跨服务查询场景。
 */
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
