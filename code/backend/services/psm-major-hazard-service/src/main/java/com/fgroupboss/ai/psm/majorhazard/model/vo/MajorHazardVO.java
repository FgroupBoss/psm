package com.fgroupboss.ai.psm.majorhazard.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MajorHazardVO {

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
}
