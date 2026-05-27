package com.fgroupboss.ai.psm.workpermit.support;

import com.fgroupboss.ai.psm.common.audit.CentralAuditClient;
import com.fgroupboss.ai.psm.workpermit.mapper.PermitStatusLogMapper;
import com.fgroupboss.ai.psm.workpermit.model.entity.PermitStatusLogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 作业票状态流转日志与中央审计双写。
 */
@Component
@RequiredArgsConstructor
public class WorkPermitAuditSupport {

    private static final String BIZ_TYPE = "WORK_PERMIT";
    private static final String BIZ_ACTION_TYPE = "WORK_PERMIT_ACTION";

    private final PermitStatusLogMapper statusLogMapper;
    private final CentralAuditClient centralAuditClient;

    public void writeStatusChange(Long tenantId, Long workPermitId, String action, String remark,
                                  String operator, String beforeStatus, String afterStatus) {
        String operatorName = normalizeOperator(operator);
        PermitStatusLogEntity log = new PermitStatusLogEntity();
        log.setTenantId(tenantId);
        log.setWorkPermitId(workPermitId);
        log.setFromStatus(beforeStatus);
        log.setToStatus(afterStatus);
        log.setAction(action);
        log.setRemark(remark);
        log.setOperatorName(operatorName);
        log.setOperatedAt(new Date());
        statusLogMapper.insert(log);

        centralAuditClient.append(CentralAuditClient.build(tenantId, operatorName, action,
                BIZ_ACTION_TYPE, workPermitId, beforeStatus, afterStatus));
        if (beforeStatus != null && afterStatus != null && !beforeStatus.equals(afterStatus)) {
            centralAuditClient.append(CentralAuditClient.build(tenantId, operatorName, action,
                    BIZ_TYPE, workPermitId, beforeStatus, afterStatus));
        }
    }

    private String normalizeOperator(String operator) {
        return StringUtils.hasText(operator) ? operator.trim() : "system";
    }
}
