package com.fgroupboss.ai.psm.realtime.alarm.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AlarmActionSummaryVO {

    private Long id;
    private String actionType;
    private String actionContent;
    private String operatorName;
    private Date operatedAt;
}
