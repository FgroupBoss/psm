package com.fgroupboss.ai.psm.majorhazard.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("major_hazard")
public class MajorHazardEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String hazardNo;
    private String name;
    private String hazardType;
    private String level;
    private Long areaId;
    private Long unitId;
    private String material;
    private String designCapacity;
    private String actualCapacity;
    private String criticalQuantity;
    private Long emergencyPlanId;
    private Long defaultInspectionPlanId;
    private String status;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
