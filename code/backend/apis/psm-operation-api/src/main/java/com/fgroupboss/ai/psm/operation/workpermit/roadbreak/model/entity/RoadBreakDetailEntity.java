package com.fgroupboss.ai.psm.operation.workpermit.roadbreak.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("road_break_detail")
public class RoadBreakDetailEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private String roadId;
    private String areaGeoJson;
    private String reason;
    private Date startAt;
    private Date endAt;
    private String responsibleUnit;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
