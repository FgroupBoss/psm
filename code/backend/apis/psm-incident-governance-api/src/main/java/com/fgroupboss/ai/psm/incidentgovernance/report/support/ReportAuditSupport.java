package com.fgroupboss.ai.psm.incidentgovernance.report.support;

import com.fgroupboss.ai.psm.common.audit.CentralAuditClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 报表导出等关键操作审计上报。
 */
@Component
@RequiredArgsConstructor
public class ReportAuditSupport {

    private final CentralAuditClient centralAuditClient;

    public void auditExport(Long tenantId, String operatorName, Long taskId, String reportType, String status) {
        centralAuditClient.append(CentralAuditClient.build(tenantId, operatorName, "REPORT_EXPORT",
                "REPORT_EXPORT_TASK", taskId, reportType, status));
    }
}
