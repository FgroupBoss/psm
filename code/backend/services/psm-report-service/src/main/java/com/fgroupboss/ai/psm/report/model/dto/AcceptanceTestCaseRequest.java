package com.fgroupboss.ai.psm.report.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AcceptanceTestCaseRequest {

    @NotNull
    private Long tenantId;
    @NotBlank
    private String caseCode;
    @NotBlank
    private String caseName;
    @NotBlank
    private String module;
    private String scenario;
    private String expectedResult;
    private String priority = "P1";
    private String status = "ACTIVE";
}
