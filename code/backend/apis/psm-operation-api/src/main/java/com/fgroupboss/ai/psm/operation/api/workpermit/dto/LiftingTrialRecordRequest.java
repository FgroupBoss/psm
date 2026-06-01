package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

import java.util.Date;

@Data
public class LiftingTrialRecordRequest {

    private Long tenantId;
    private String trialResult;
    private String issueDesc;
    private String attachmentRef;
    private Date trialAt;
}
