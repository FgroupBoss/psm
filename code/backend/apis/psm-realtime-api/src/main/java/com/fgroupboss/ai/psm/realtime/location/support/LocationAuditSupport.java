package com.fgroupboss.ai.psm.realtime.location.support;

import com.fgroupboss.ai.psm.common.AuditBizType;
import com.fgroupboss.ai.psm.common.audit.CentralAuditClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 定位事件中央审计双写（best-effort）。
 */
@Component
@RequiredArgsConstructor
public class LocationAuditSupport {

    private final CentralAuditClient centralAuditClient;

    public void append(Long tenantId, Long eventId, String action, String eventType) {
        centralAuditClient.append(CentralAuditClient.build(
                tenantId, "system", action, AuditBizType.LOCATION_EVENT.name(), eventId, null, eventType));
    }
}
