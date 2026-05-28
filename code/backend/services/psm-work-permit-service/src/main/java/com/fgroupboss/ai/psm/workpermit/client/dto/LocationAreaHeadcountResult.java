package com.fgroupboss.ai.psm.workpermit.client.dto;

import lombok.Data;

@Data
public class LocationAreaHeadcountResult {

    private Long tenantId;
    private Long areaId;
    private int headcount;
    private int onlineCount;
}
