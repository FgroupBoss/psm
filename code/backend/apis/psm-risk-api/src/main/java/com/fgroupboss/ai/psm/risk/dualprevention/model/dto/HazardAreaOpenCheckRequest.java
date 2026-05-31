package com.fgroupboss.ai.psm.risk.dualprevention.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HazardAreaOpenCheckRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "areaId is required")
    private Long areaId;

    /** 最低阻断等级，默认 MAJOR（较大及以上）。 */
    private String minLevel;
}
