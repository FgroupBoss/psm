package com.fgroupboss.ai.psm.identity.notification.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NotificationChannelProperties.class)
public class NotificationModuleConfig {
}
