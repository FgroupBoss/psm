package com.fgroupboss.ai.psm.risk.inspection.support;

import com.fgroupboss.ai.psm.common.AuditBizType;
import com.fgroupboss.ai.psm.common.audit.CentralAuditClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 巡检任务中央审计双写（best-effort）。
 */
@Component
@RequiredArgsConstructor
public class InspectionAuditSupport {

    private final CentralAuditClient centralAuditClient;

    public void append(Long tenantId, Long taskId, String action, String before, String after, String operator) {
        centralAuditClient.append(CentralAuditClient.build(
                tenantId, normalizeOperator(operator), action,
                AuditBizType.INSPECTION_TASK.name(), taskId, before, after));
    }

    private String normalizeOperator(String operator) {
        return StringUtils.hasText(operator) ? operator.trim() : "system";
    }
}
