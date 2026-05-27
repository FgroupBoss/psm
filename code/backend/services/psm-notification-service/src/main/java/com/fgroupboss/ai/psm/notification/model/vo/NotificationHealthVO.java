package com.fgroupboss.ai.psm.notification.model.vo;

import lombok.Data;

@Data
public class NotificationHealthVO {

    private String service;
    private String module;
    private String version;
    private Long messageCount;
}
