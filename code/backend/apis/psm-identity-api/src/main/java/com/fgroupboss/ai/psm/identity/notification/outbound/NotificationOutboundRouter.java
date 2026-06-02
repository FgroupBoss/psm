package com.fgroupboss.ai.psm.identity.notification.outbound;

import com.fgroupboss.ai.psm.identity.notification.config.NotificationChannel;
import com.fgroupboss.ai.psm.identity.notification.config.NotificationChannelProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 按通道配置选择 HTTP 或占位投递。
 */
@Component
@RequiredArgsConstructor
public class NotificationOutboundRouter {

    private final NotificationChannelProperties properties;
    private final HttpNotificationOutboundSender httpSender;
    private final PlaceholderNotificationOutboundSender placeholderSender;

    /**
     * 按通道配置选择 HTTP 网关或占位投递。
     *
     * @param request 外通道请求
     * @return 投递结果（SENT / SKIPPED / FAILED）
     */
    public OutboundSendResult dispatch(OutboundSendRequest request) {
        String channel = request.getChannel();
        if (!StringUtils.hasText(channel) || NotificationChannel.IN_APP.equals(channel)) {
            return OutboundSendResult.skipped("in-app only");
        }
        NotificationChannelProperties.ChannelSettings settings = properties.settingsFor(channel);
        if (settings != null && settings.isEnabled() && "HTTP".equalsIgnoreCase(settings.getMode())) {
            return httpSender.sendViaHttp(channel, request);
        }
        return placeholderSender.sendPlaceholder(channel, request);
    }
}
