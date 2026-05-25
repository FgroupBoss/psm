package com.fgroupboss.ai.psm.audit.service;

import com.fgroupboss.ai.psm.audit.model.AuditLogRecord;
import com.fgroupboss.ai.psm.audit.repository.AuditLogRepository;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public PageResult<AuditLogRecord> page(Long tenantId, String bizType, Long bizId, String action,
                                           String operatorName, LocalDateTime startTime, LocalDateTime endTime,
                                           int pageNo, int pageSize) {
        if (tenantId == null || tenantId.longValue() <= 0L) {
            throw new BusinessException(400, "tenantId is required");
        }
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (normalizedPageNo - 1) * normalizedPageSize;
        long total = repository.count(tenantId, bizType, bizId, action, operatorName, startTime, endTime);
        List<AuditLogRecord> records = repository.list(tenantId, bizType, bizId, action, operatorName,
                startTime, endTime, normalizedPageSize, offset);
        return new PageResult<AuditLogRecord>(total, normalizedPageNo, normalizedPageSize, records);
    }
}
