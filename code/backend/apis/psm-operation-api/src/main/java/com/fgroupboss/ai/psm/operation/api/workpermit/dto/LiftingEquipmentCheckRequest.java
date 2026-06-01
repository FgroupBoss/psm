package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

@Data
public class LiftingEquipmentCheckRequest {

    private Long tenantId;
    private String equipmentType;
    private String equipmentNo;
    private String itemCode;
    private String itemName;
    private String checkResult;
    private String attachmentRef;
}
