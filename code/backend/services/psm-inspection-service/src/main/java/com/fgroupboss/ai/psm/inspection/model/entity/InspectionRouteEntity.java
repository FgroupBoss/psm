package com.fgroupboss.ai.psm.inspection.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("insp_route")
public class InspectionRouteEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String routeCode;
    private String routeName;
    private Long areaId;
    private Integer estimatedMinutes;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
