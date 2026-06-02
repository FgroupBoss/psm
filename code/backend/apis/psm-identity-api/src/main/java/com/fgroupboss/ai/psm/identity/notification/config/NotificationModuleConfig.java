package com.fgroupboss.ai.psm.identity.notification.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 消息模块 Spring 配置：绑定 {@link NotificationChannelProperties}。
 */
@Configuration
@EnableConfigurationProperties(NotificationChannelProperties.class)
public class NotificationModuleConfig {
}
