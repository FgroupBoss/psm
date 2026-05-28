package com.fgroupboss.ai.psm.inspection.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("insp_route_point")
public class RoutePointEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long routeId;
    private String pointCode;
    private String pointName;
    private String signType;
    private String signCode;
    private Long areaId;
    private Long checklistTemplateId;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
