package com.fgroupboss.ai.psm.alarm.support;

import com.fgroupboss.ai.psm.alarm.mapper.AlarmActionRecordMapper;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmActionRecordEntity;
import com.fgroupboss.ai.psm.common.AuditBizType;
import com.fgroupboss.ai.psm.common.audit.CentralAuditClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 报警处置流水与中央审计双写。
 */
@Component
@RequiredArgsConstructor
public class AlarmAuditSupport {

    private final AlarmActionRecordMapper actionRecordMapper;
    private final CentralAuditClient centralAuditClient;

    public void writeAction(Long tenantId, Long alarmEventId, String actionType, String content,
                            String operator, String beforeStatus, String afterStatus) {
        String operatorName = normalizeOperator(operator);
        AlarmActionRecordEntity record = new AlarmActionRecordEntity();
        record.setTenantId(tenantId);
        record.setAlarmEventId(alarmEventId);
        record.setActionType(actionType);
        record.setActionContent(content);
        record.setOperatorName(operatorName);
        record.setOperatedAt(new Date());
        actionRecordMapper.insert(record);

        centralAuditClient.append(CentralAuditClient.build(tenantId, operatorName, actionType,
                AuditBizType.ALARM_ACTION.name(), alarmEventId, beforeStatus, afterStatus));
        if (beforeStatus != null && afterStatus != null && !beforeStatus.equals(afterStatus)) {
            centralAuditClient.append(CentralAuditClient.build(tenantId, operatorName, actionType,
                    AuditBizType.ALARM.name(), alarmEventId, beforeStatus, afterStatus));
        }
    }

    private String normalizeOperator(String operator) {
        return StringUtils.hasText(operator) ? operator.trim() : "system";
    }
}
