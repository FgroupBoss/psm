package com.fgroupboss.ai.psm.risk.majorhazard.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HazardPointRequest {

    @NotNull(message = "monitorPointId is required")
    private Long monitorPointId;
    private String pointCode;
    private String pointName;
}
