package com.fgroupboss.ai.psm.alarm.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AlarmAreaActiveCheckVO {

    private boolean hasBlocking;
    private int count;
    private List<AlarmEventVO> alarms = new ArrayList<AlarmEventVO>();
}
