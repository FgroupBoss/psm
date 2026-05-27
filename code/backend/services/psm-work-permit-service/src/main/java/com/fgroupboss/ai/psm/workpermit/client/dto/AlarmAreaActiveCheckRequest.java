package com.fgroupboss.ai.psm.workpermit.client.dto;

import lombok.Data;

@Data
public class AlarmAreaActiveCheckRequest {

    private Long tenantId;
    private Long areaId;
    private String minLevel;
}
