package com.fgroupboss.ai.psm.report.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class AlarmReportSummaryVO {

    private long totalCount;
    private long actionableCount;
    private long closedCount;
    private long falseClosedCount;
    private long unconfirmedCount;
    private long inProgressCount;
    /** 报警闭环率 = closedCount / actionableCount */
    private double closureRate;
    /** 误报率 = falseClosedCount / totalCount */
    private double falseAlarmRate;
    private Double avgConfirmMinutes;
    private Double avgDisposeMinutes;
    private Map<String, Long> levelCounts = new LinkedHashMap<String, Long>();
    private Map<String, Long> statusCounts = new LinkedHashMap<String, Long>();
    private Date dataRefreshedAt;
    private String dataSource;
}
