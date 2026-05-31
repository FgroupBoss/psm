package com.fgroupboss.ai.psm.identity.notification.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationMessageVO {

    private Long id;
    private Long tenantId;
    private Long userId;
    private String channel;
    private String templateCode;
    private String title;
    private String content;
    private String bizType;
    private Long bizId;
    private Boolean read;
    private Date readAt;
    private Date createdAt;
}
