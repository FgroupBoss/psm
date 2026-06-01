package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

@Data
public class HeightWorkProtectionCheckRequest {

    private Long tenantId;
    private String checkStage;
    private String itemCode;
    private String itemName;
    private Boolean requiredFlag;
    private String checkResult;
    private String attachmentRef;
    private String locationText;
}
