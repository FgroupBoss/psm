package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

@Data
public class ConfinedSpaceRescuePlanRequest {

    private Long tenantId;
    private String planRef;
    private String contact;
    private String equipmentJson;
    private Boolean confirm;
}
