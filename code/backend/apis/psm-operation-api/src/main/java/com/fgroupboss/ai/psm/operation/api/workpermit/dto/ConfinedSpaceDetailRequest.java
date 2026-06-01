package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

@Data
public class ConfinedSpaceDetailRequest {

    private Long tenantId;
    private Long spaceId;
    private String spaceName;
    private Integer entryCount;
    private String ventilationType;
    private Boolean continuousMonitoring;
}
