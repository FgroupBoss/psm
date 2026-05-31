package com.fgroupboss.ai.psm.risk.inspection.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RoutePointRequest {

    @NotBlank(message = "pointCode is required")
    private String pointCode;
    @NotBlank(message = "pointName is required")
    private String pointName;
    @NotBlank(message = "signType is required")
    private String signType;
    private String signCode;
    private Long areaId;
    private Long checklistTemplateId;
    private Integer sortOrder;
}
