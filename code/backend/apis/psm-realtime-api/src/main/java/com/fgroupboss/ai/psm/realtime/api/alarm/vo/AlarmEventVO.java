package com.fgroupboss.ai.psm.realtime.api.alarm.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AlarmEventVO {

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
    private Long unitId;
    private Long equipmentId;
    private Long monitorPointId;
    private Long hazardId;
    private Integer occurrenceCount;
    private Date firstOccurredAt;
    private Date lastOccurredAt;
}

