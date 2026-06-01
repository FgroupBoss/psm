package com.fgroupboss.ai.psm.risk.majorhazard.support;

import com.fgroupboss.ai.psm.common.audit.CentralAuditClient;
import com.fgroupboss.ai.psm.risk.majorhazard.mapper.MajorHazardAuditRecordMapper;
import com.fgroupboss.ai.psm.risk.majorhazard.model.entity.MajorHazardAuditRecordEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 危险源审计流水写入（点位/附件等子资源复用）。
 */
@Component
@RequiredArgsConstructor
public class MajorHazardAuditSupport {

    private final MajorHazardAuditRecordMapper auditRecordMapper;
    private final CentralAuditClient centralAuditClient;

    public void write(Long tenantId, Long targetId, String targetType, String action,
                      String opinion, String operator) {
        MajorHazardAuditRecordEntity record = new MajorHazardAuditRecordEntity();
        record.setTenantId(tenantId);
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        record.setAction(action);
        record.setOpinion(opinion);
        record.setOperatorName(StringUtils.hasText(operator) ? operator.trim() : "system");
        record.setOperatedAt(LocalDateTime.now());
        auditRecordMapper.insert(record);
        centralAuditClient.append(CentralAuditClient.build(tenantId, record.getOperatorName(), action,
                targetType, targetId, null, opinion));
    }
}
