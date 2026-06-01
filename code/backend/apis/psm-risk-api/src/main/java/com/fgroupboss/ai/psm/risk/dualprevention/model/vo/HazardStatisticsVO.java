package com.fgroupboss.ai.psm.risk.dualprevention.model.vo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class HazardStatisticsVO {

    private long totalCount;
    private long overdueCount;
    private long closedCount;
    private Map<String, Long> statusCounts = new LinkedHashMap<String, Long>();
    private Map<String, Long> levelCounts = new LinkedHashMap<String, Long>();
}
