package com.fgroupboss.ai.psm.incidentgovernance.report.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AcceptanceTestCaseVO {

    private Long id;
    private Long tenantId;
    private String caseCode;
    private String caseName;
    private String module;
    private String scenario;
    private String expectedResult;
    private String priority;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
