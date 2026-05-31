package com.fgroupboss.ai.psm.risk.inspection.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InspectionStatisticsVO {

    private long totalCount;
    private long pendingCount;
    private long inProgressCount;
    private long completedCount;
    private long missedCount;
    private long abnormalItemCount;
    private BigDecimal completionRate;
    private BigDecimal missedRate;
    private BigDecimal abnormalRate;
}
