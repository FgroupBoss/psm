package com.fgroupboss.ai.psm.identity.notification.outbound;

import com.fgroupboss.ai.psm.identity.notification.config.NotificationChannelProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 占位发送器：通道已启用但未配置 HTTP 网关时使用，便于后期切换为 HTTP 模式。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceholderNotificationOutboundSender {

    private final NotificationChannelProperties properties;

    public OutboundSendResult sendPlaceholder(String channel, OutboundSendRequest request) {
        NotificationChannelProperties.ChannelSettings settings = properties.settingsFor(channel);
        if (settings == null || !settings.isEnabled()) {
            return OutboundSendResult.skipped("channel disabled");
        }
        if ("HTTP".equalsIgnoreCase(settings.getMode())) {
            return OutboundSendResult.skipped("use HttpNotificationOutboundSender");
        }
        log.debug("placeholder outbound channel={} requestId={}", channel, request.getRequestId());
        return OutboundSendResult.skipped("PLACEHOLDER mode, configure psm.notification.*.mode=HTTP and http-url");
    }
}
