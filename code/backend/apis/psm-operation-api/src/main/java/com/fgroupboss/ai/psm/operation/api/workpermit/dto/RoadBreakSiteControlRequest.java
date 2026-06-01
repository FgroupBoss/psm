package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

@Data
public class RoadBreakSiteControlRequest {

    private Long tenantId;
    private String itemCode;
    private String itemName;
    private String position;
    private String checkResult;
    private String attachmentRef;
}
