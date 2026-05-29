package com.fgroupboss.ai.psm.realtime.api.alarm.dto;

import lombok.Data;

@Data
public class AlarmActionRequest {

    private String content;
    private String assignee;
}

