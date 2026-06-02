package com.fgroupboss.ai.psm.identity.notification.outbound;

/**
 * 外通道发送适配器（短信/邮件/企微/钉钉）。
 */
public interface NotificationOutboundSender {

    String channel();

    OutboundSendResult send(OutboundSendRequest request);
}
