package com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 对账汇总：按平台/数据域统计成功率。
 */
@Data
public class RegReconciliationVO {

    private Long tenantId;
    private long totalTasks;
    private long successTasks;
    private long failedTasks;
    private double successRate;
    private List<RegReconciliationItemVO> items;

    @Data
    public static class RegReconciliationItemVO {
        private String platformCode;
        private String dataDomain;
        private long total;
        private long success;
        private long failed;
        private double successRate;
    }
}
