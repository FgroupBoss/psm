package com.fgroupboss.ai.psm.identity.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息外通道配置：填写 HTTP 网关地址或启用占位模式即可对接第三方。
 */
@Data
@ConfigurationProperties(prefix = "psm.notification")
public class NotificationChannelProperties {

    /** 未指定 channels 时除站内信外是否尝试外通道（需 enabled=true 且非 PLACEHOLDER）。 */
    private boolean autoExternal = false;

    private ChannelSettings sms = new ChannelSettings();
    private ChannelSettings email = new ChannelSettings();
    private ChannelSettings wechat = new ChannelSettings();
    private ChannelSettings dingtalk = new ChannelSettings();

    @Data
    public static class ChannelSettings {
        /** 是否启用该通道。 */
        private boolean enabled = false;
        /**
         * PLACEHOLDER：仅写投递日志为 SKIPPED，便于联调；
         * HTTP：向 httpUrl POST 标准 JSON，由企业消息网关转发至短信/邮件/IM 厂商。
         */
        private String mode = "PLACEHOLDER";
        private String httpUrl = "";
        private String apiKey = "";
        private String apiSecret = "";
        /** 短信签名（厂商报备）。 */
        private String signName = "";
        /** 邮件发件人。 */
        private String fromAddress = "";
        /** 企微 corpId / 钉钉 appKey 等，随 HTTP 请求体一并提交给网关。 */
        private String corpId = "";
        private String agentId = "";
        private String appKey = "";
    }

    public ChannelSettings settingsFor(String channel) {
        if (NotificationChannel.SMS.equals(channel)) {
            return sms;
        }
        if (NotificationChannel.EMAIL.equals(channel)) {
            return email;
        }
        if (NotificationChannel.WECHAT.equals(channel)) {
            return wechat;
        }
        if (NotificationChannel.DINGTALK.equals(channel)) {
            return dingtalk;
        }
        return null;
    }
}
