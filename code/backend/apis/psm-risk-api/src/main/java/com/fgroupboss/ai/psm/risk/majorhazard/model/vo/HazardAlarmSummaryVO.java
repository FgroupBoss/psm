package com.fgroupboss.ai.psm.risk.majorhazard.model.vo;

import lombok.Data;

@Data
public class HazardAlarmSummaryVO {

    private Long id;
    private String alarmNo;
    private String title;
    private String alarmLevel;
    private String status;
    private Integer occurrenceCount;
    private String lastOccurredAt;
}
