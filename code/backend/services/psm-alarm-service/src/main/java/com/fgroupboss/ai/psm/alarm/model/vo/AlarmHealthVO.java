package com.fgroupboss.ai.psm.alarm.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmHealthVO {

    private String service;
    private String module;
    private String version;
    private long seedEventCount;
}
