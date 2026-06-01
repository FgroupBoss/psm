package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

@Data
public class BlindPlateActionConfirmRequest {

    private Long tenantId;
    private String toStatus;
    private String attachmentRef;
}
