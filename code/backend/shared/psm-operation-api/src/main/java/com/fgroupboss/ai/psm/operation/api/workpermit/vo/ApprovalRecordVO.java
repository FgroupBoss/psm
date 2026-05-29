package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ApprovalRecordVO {

    private Long id;
    private String action;
    private String opinion;
    private String operatorName;
    private Date operatedAt;
}

