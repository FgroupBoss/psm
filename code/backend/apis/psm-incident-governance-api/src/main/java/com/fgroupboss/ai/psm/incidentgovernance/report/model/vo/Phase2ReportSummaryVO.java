package com.fgroupboss.ai.psm.incidentgovernance.report.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 二期模块聚合报表摘要。
 */
@Data
public class Phase2ReportSummaryVO {

    private Long tenantId;
    private LocalDateTime generatedAt;
    private String dataSource;

    private DualPreventionSection dualPrevention;
    private InspectionSection inspection;
    private LocationSection location;
    private VideoSection video;
    private SimopsSection simops;

    @Data
    public static class DualPreventionSection {
        private long totalCount;
        private long overdueCount;
        private long closedCount;
    }

    @Data
    public static class InspectionSection {
        private long totalCount;
        private long completedCount;
        private long missedCount;
        private long abnormalItemCount;
    }

    @Data
    public static class LocationSection {
        private long eventCount;
    }

    @Data
    public static class VideoSection {
        private long aiEventCount;
    }

    @Data
    public static class SimopsSection {
        private long totalScans;
        private long totalConflicts;
        private long blockCount;
        private long coordinateCount;
        private long warnCount;
    }
}
