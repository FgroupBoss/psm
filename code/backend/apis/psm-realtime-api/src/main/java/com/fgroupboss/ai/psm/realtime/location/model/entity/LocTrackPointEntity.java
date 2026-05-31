package com.fgroupboss.ai.psm.realtime.location.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("loc_track_point")
public class LocTrackPointEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String tagNo;
    private Long personId;
    private Long areaId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime recordedAt;
    private LocalDateTime createdAt;
}
