package com.fgroupboss.ai.psm.realtime.alarm.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("alarm_escalation_record")
public class AlarmEscalationRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long alarmEventId;
    private String escalationLevel;
    private String reason;
    private String operatorName;
    private Date escalatedAt;
}
