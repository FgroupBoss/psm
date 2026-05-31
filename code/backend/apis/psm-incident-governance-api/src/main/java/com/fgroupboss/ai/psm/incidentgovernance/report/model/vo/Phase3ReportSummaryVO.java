package com.fgroupboss.ai.psm.incidentgovernance.report.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Phase3ReportSummaryVO {
    private Long tenantId;
    private LocalDateTime generatedAt;
    private String dataSource;
    private Map<String, Object> governanceDashboard;
    private Map<String, Object> pha;
    private Map<String, Object> moc;
    private Map<String, Object> pssr;
    private Map<String, Object> barrier;
    private Map<String, Object> incident;
}
