package com.fgroupboss.ai.psm.identity.notification.model.vo;

import lombok.Data;

/** 外通道启用与配置摘要（管理端展示，不含密钥）。 */
@Data
public class NotificationChannelStatusVO {

    private String channel;
    private boolean enabled;
    private String mode;
    private boolean configured;
}
