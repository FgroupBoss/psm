package com.fgroupboss.ai.psm.realtime.alarm.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("alarm_notification_record")
public class AlarmNotificationRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long alarmEventId;
    private String channel;
    private String notifyTarget;
    private String notifyContent;
    private String status;
    private Date sentAt;
    private Date createdAt;
}
