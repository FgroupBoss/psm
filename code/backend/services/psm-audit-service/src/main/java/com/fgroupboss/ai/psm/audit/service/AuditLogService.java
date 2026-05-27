package com.fgroupboss.ai.psm.audit.service;

import com.fgroupboss.ai.psm.audit.model.dto.AuditLogIngestRequest;
import com.fgroupboss.ai.psm.audit.model.vo.AuditLogRecordVO;
import com.fgroupboss.ai.psm.common.PageResult;

import java.time.LocalDateTime;

/**
 * 审计日志查询与写入服务。
 */
public interface AuditLogService {

    PageResult<AuditLogRecordVO> page(Long tenantId, String bizType, String bizTypePrefix, Long bizId, String action,
                                      String operatorName, LocalDateTime startTime, LocalDateTime endTime,
                                      int pageNo, int pageSize);

    void append(AuditLogIngestRequest request);
}
