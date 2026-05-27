package com.fgroupboss.ai.psm.alarm.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class AlarmDetailVO {

    private AlarmEventVO event;
    private List<AlarmOccurrenceVO> occurrences;
    private List<AlarmActionSummaryVO> actions;
}
