package com.fgroupboss.ai.psm.report.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ReportExportRequest {

    @NotNull
    private Long tenantId;
    @NotBlank
    private String reportType;
    private String exportFormat = "CSV";
    private String queryParamsJson;
    private String requestedBy;
}
