package com.fgroupboss.ai.psm.incidentgovernance.report.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class WorkPermitReportDetailVO {

    private Long id;
    private String permitNo;
    private String workType;
    private String status;
    private String title;
    private Long areaId;
    private Long contractorCompanyId;
    private boolean sitePermitRequired;
    private boolean siteTraceComplete;
    private boolean contractorInvolved;
    private boolean contractorEligibilityChecked;
    private Date planStartAt;
    private Date planEndAt;
    private Date createdAt;
}
