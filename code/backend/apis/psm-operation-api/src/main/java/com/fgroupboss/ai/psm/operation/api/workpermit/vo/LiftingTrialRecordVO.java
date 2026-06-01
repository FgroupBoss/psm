package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class LiftingTrialRecordVO {

    private Long id;
    private String trialResult;
    private String issueDesc;
    private String confirmedBy;
    private String attachmentRef;
    private Date trialAt;
}
