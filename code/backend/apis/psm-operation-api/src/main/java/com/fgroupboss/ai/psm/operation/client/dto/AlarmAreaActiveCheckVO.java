package com.fgroupboss.ai.psm.operation.client.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AlarmAreaActiveCheckVO {

    private boolean hasBlocking;
    private int count;
    private List<AlarmEventVO> alarms = new ArrayList<AlarmEventVO>();
}

