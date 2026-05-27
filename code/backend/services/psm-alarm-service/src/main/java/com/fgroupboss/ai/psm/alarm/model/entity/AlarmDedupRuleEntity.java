package com.fgroupboss.ai.psm.alarm.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("alarm_dedup_rule")
public class AlarmDedupRuleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String ruleCode;
    private String sourceType;
    private Integer windowSeconds;
    private Integer enabled;
    private Date createdAt;
    private Date updatedAt;
}
