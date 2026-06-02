package com.fgroupboss.ai.psm.identity.notification.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("notification_delivery_log")
public class NotificationDeliveryLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long messageId;
    private String requestId;
    private String channel;
    private String status;
    private String providerMsgId;
    private String errorCode;
    private String errorMessage;
    private Date sentAt;
    private Date createdAt;
}
