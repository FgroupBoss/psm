package com.fgroupboss.ai.psm.operation.workpermit.roadbreak.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("road_break_traffic_plan")
public class RoadBreakTrafficPlanEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String detourGeoJson;
    private String emergencyLaneGeoJson;
    private String planRef;
    private Integer confirmed;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
