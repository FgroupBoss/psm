package com.fgroupboss.ai.psm.barrier.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MiEquipmentRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "equipmentCode is required")
    private String equipmentCode;
    @NotBlank(message = "equipmentName is required")
    private String equipmentName;
    private Long areaId;
    private Integer criticalFlag;
    private String status;
}
