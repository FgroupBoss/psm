package com.fgroupboss.ai.psm.realtime.location.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("loc_geofence")
public class LocGeofenceEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String fenceCode;
    private String fenceName;
    private Long areaId;
    private String fenceType;
    private String geometryJson;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
