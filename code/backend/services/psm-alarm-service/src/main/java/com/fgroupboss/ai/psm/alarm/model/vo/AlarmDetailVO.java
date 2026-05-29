package com.fgroupboss.ai.psm.alarm.model.vo;

import com.fgroupboss.ai.psm.realtime.api.alarm.vo.AlarmEventVO;
import lombok.Data;

import java.util.List;

@Data
public class AlarmDetailVO {

    private AlarmEventVO event;
    private List<AlarmOccurrenceVO> occurrences;
    private List<AlarmActionSummaryVO> actions;
}
