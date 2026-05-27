package com.fgroupboss.ai.psm.report.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AcceptanceTestRunVO {

    private Long id;
    private Long tenantId;
    private Long caseId;
    private String runNo;
    private String executorName;
    private String runStatus;
    private String evidenceRef;
    private String remark;
    private Date executedAt;
    private Date createdAt;
}
