package com.fgroupboss.ai.psm.location.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("loc_event")
public class LocEventEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String eventType;
    private String tagNo;
    private Long personId;
    private Long areaId;
    private Long fenceId;
    private LocalDateTime eventTime;
    private Long alarmId;
    private Long workPermitId;
    private String rawPayload;
    private LocalDateTime createdAt;
}
