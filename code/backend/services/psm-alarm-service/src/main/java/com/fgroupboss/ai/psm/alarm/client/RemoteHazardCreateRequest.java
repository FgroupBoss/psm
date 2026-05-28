package com.fgroupboss.ai.psm.alarm.client;

import lombok.Data;

@Data
public class RemoteHazardCreateRequest {

    private Long tenantId;
    private String hazardLevel;
    private String sourceType;
    private Long sourceBizId;
    private Long riskUnitId;
    private Long areaId;
    private String description;
}
