package com.fgroupboss.ai.psm.majorhazard.client.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AlarmAreaActiveCheckResult {

    private boolean hasBlocking;
    private int count;
    private List<AlarmEventSummary> alarms = new ArrayList<AlarmEventSummary>();
}
