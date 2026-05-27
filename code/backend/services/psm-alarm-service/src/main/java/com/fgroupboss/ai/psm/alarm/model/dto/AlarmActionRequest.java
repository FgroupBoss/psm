package com.fgroupboss.ai.psm.alarm.model.dto;

import lombok.Data;

@Data
public class AlarmActionRequest {

    private String content;
    private String assignee;
}
