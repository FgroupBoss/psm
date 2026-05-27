package com.fgroupboss.ai.psm.majorhazard.client.dto;

import lombok.Data;

@Data
public class AlarmAreaActiveCheckRequest {

    private Long tenantId;
    private Long areaId;
    private String minLevel;
}
