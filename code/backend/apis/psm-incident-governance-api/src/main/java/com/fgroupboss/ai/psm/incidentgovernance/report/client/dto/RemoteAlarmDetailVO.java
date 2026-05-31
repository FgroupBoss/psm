package com.fgroupboss.ai.psm.incidentgovernance.report.client.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RemoteAlarmDetailVO {

    private RemoteAlarmEventVO event;
    private List<RemoteAlarmActionVO> actions = new ArrayList<RemoteAlarmActionVO>();
}
