package com.fgroupboss.ai.psm.audit.service;

import com.fgroupboss.ai.psm.audit.mapper.AuditChangeLogMapper;
import com.fgroupboss.ai.psm.audit.model.dto.AuditLogIngestRequest;
import com.fgroupboss.ai.psm.audit.model.entity.AuditChangeLogEntity;
import com.fgroupboss.ai.psm.audit.service.impl.AuditLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuditLogServiceImplTest {

    private AuditChangeLogMapper auditChangeLogMapper;
    private AuditLogService service;

    @BeforeEach
    void setUp() {
        auditChangeLogMapper = mock(AuditChangeLogMapper.class);
        service = new AuditLogServiceImpl(auditChangeLogMapper);
    }

    @Test
    void appendShouldInsertAuditLog() {
        AuditLogIngestRequest request = new AuditLogIngestRequest();
        request.setTenantId(1L);
        request.setAction("PUBLISH");
        request.setBizType("MAJOR_HAZARD");
        request.setBizId(10L);
        request.setOperatorName("admin");

        service.append(request);

        verify(auditChangeLogMapper).insert(any(AuditChangeLogEntity.class));
    }
}
