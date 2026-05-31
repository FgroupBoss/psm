package com.fgroupboss.ai.psm.realtime.location.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("loc_realtime")
public class LocRealtimeEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String tagNo;
    private Long personId;
    private Long areaId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String onlineStatus;
    private LocalDateTime lastSeenAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
