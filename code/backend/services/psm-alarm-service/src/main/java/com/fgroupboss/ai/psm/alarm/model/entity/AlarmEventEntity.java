package com.fgroupboss.ai.psm.alarm.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("alarm_event")
public class AlarmEventEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String alarmNo;
    private String sourceType;
    private String sourceCode;
    private String title;
    private String content;
    private String alarmLevel;
    private String status;
    private Long areaId;
    private Long unitId;
    private Long equipmentId;
    private Long monitorPointId;
    private Long hazardId;
    private String dedupKey;
    private Integer occurrenceCount;
    private Date firstOccurredAt;
    private Date lastOccurredAt;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
