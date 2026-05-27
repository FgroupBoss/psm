package com.fgroupboss.ai.psm.mobile.client.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ApprovalRecordVO {

    private Long id;
    private String actionType;
    private String operatorName;
    private String opinion;
    private Date actionAt;
}
