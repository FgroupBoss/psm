package com.fgroupboss.ai.psm.risk.majorhazard.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RiskContextVO {

    private Long areaId;
    private Long unitId;
    private List<RiskContextHazardVO> hazards = new ArrayList<RiskContextHazardVO>();
    private String maxLevel;
    private boolean blockingAlarm;
    private String blockingReason;
}
