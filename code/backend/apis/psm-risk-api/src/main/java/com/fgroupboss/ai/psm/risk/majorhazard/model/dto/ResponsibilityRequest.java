package com.fgroupboss.ai.psm.risk.majorhazard.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ResponsibilityRequest {

    @NotBlank(message = "responsibilityType is required")
    private String responsibilityType;
    @NotBlank(message = "personName is required")
    private String personName;
    private String personPhone;
    private Long personId;
    private Integer sortNo;
}
