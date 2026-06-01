package com.fgroupboss.ai.psm.risk.majorhazard.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MajorHazardRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "hazardNo is required")
    private String hazardNo;
    @NotBlank(message = "name is required")
    private String name;
    private String hazardType;
    @NotBlank(message = "level is required")
    private String level;
    private Long areaId;
    private Long unitId;
    private String material;
    private String designCapacity;
    private String actualCapacity;
    private String criticalQuantity;
    private Long emergencyPlanId;
}
