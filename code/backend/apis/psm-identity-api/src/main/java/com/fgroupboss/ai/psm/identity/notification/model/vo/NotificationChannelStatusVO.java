package com.fgroupboss.ai.psm.identity.notification.model.vo;

import lombok.Data;

@Data
public class NotificationChannelStatusVO {

    private String channel;
    private boolean enabled;
    private String mode;
    private boolean configured;
}
