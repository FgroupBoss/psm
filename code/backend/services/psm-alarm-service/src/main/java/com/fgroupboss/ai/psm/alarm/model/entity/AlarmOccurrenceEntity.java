package com.fgroupboss.ai.psm.alarm.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("alarm_occurrence")
public class AlarmOccurrenceEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long alarmEventId;
    private Date occurredAt;
    private String rawValue;
    private String snapshotJson;
    private Date createdAt;
}
