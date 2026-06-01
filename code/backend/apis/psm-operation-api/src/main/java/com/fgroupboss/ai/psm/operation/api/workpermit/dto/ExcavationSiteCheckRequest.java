package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

@Data
public class ExcavationSiteCheckRequest {

    private Long tenantId;
    private String stage;
    private String itemCode;
    private String itemName;
    private String checkResult;
    private String attachmentRef;
}
