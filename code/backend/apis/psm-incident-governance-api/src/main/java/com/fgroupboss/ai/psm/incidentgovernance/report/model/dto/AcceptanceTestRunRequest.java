package com.fgroupboss.ai.psm.incidentgovernance.report.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class AcceptanceTestRunRequest {

    @NotNull
    private Long tenantId;
    @NotNull
    private Long caseId;
    @NotBlank
    private String runNo;
    private String executorName;
    @NotBlank
    private String runStatus;
    private String evidenceRef;
    private String remark;
    private Date executedAt;
}
