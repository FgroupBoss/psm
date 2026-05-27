package com.fgroupboss.ai.psm.report.client.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RemoteAlarmEventVO {

    private Long id;
    private Long tenantId;
    private String alarmNo;
    private String sourceType;
    private String alarmLevel;
    private String status;
    private Long areaId;
    private Long hazardId;
    private Date firstOccurredAt;
    private Date lastOccurredAt;
}
