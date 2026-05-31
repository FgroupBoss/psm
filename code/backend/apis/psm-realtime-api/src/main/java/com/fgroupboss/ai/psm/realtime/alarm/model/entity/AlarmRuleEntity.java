package com.fgroupboss.ai.psm.realtime.alarm.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("alarm_rule")
public class AlarmRuleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String ruleCode;
    private String ruleName;
    private String ruleType;
    private String configJson;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
