package com.fgroupboss.ai.psm.realtime.api.alarm.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 区域活跃报警检查结果，用于作业、风险等业务域阻断校验。
 */
@Data
public class AlarmAreaActiveCheckResult {

    private boolean hasBlocking;
    private int count;
    private List<AlarmEventSummary> alarms = new ArrayList<AlarmEventSummary>();
}
