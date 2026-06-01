package com.fgroupboss.ai.psm.realtime.alarm.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AlarmOccurrenceVO {

    private Long id;
    private Date occurredAt;
    private String rawValue;
}
