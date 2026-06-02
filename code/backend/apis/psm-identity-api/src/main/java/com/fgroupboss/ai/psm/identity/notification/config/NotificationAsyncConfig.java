package com.fgroupboss.ai.psm.identity.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 启用异步执行，供 {@link com.fgroupboss.ai.psm.identity.notification.service.NotificationExternalDeliveryService} 外通道投递使用。
 */
@Configuration
@EnableAsync
public class NotificationAsyncConfig {
}
