package com.fgroupboss.ai.psm.operation.client.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AlarmAreaActiveCheckRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "areaId is required")
    private Long areaId;

    /** 鏈€浣庨樆鏂瓑绾э紝榛樿 LEVEL_2锛堝惈 LEVEL_1銆丩EVEL_2锛夈€?*/
    private String minLevel;
}

