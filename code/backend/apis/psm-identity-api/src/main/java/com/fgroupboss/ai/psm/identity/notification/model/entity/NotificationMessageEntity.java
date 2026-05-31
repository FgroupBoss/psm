package com.fgroupboss.ai.psm.identity.notification.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("notification_message")
public class NotificationMessageEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long userId;
    private String requestId;
    private String channel;
    private String templateCode;
    private String title;
    private String content;
    private String bizType;
    private Long bizId;
    private Integer readFlag;
    private Date readAt;
    private Date createdAt;
}
