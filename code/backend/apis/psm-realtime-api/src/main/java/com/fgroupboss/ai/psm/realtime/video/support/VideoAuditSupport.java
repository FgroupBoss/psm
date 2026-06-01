package com.fgroupboss.ai.psm.realtime.video.support;

import com.fgroupboss.ai.psm.common.AuditBizType;
import com.fgroupboss.ai.psm.common.audit.CentralAuditClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 视频 AI 事件中央审计双写（best-effort）。
 */
@Component
@RequiredArgsConstructor
public class VideoAuditSupport {

    private final CentralAuditClient centralAuditClient;

    public void append(Long tenantId, Long eventId, String action, String before, String after, String operator) {
        centralAuditClient.append(CentralAuditClient.build(
                tenantId, normalizeOperator(operator), action,
                AuditBizType.VIDEO_AI_EVENT.name(), eventId, before, after));
    }

    private String normalizeOperator(String operator) {
        return StringUtils.hasText(operator) ? operator.trim() : "system";
    }
}
