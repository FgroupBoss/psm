package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class HeightWorkProtectionCheckVO {

    private Long id;
    private String checkStage;
    private String itemCode;
    private String itemName;
    private Boolean requiredFlag;
    private String checkResult;
    private String attachmentRef;
    private String locationText;
    private String checkedBy;
    private Date checkedAt;
}
