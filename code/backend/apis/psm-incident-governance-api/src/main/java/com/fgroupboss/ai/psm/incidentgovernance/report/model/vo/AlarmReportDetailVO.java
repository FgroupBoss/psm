package com.fgroupboss.ai.psm.incidentgovernance.report.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AlarmReportDetailVO {

    private Long id;
    private String alarmNo;
    private String alarmLevel;
    private String status;
    private String sourceType;
    private Long areaId;
    private Long hazardId;
    private Long confirmDurationMinutes;
    private Long disposeDurationMinutes;
    private Date firstOccurredAt;
    private Date lastOccurredAt;
}
