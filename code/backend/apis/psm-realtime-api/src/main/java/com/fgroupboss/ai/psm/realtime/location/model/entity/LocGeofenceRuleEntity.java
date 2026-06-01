package com.fgroupboss.ai.psm.realtime.location.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("loc_geofence_rule")
public class LocGeofenceRuleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long fenceId;
    private String ruleType;
    private Integer thresholdValue;
    private Integer enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
