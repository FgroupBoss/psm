package com.fgroupboss.ai.psm.risk.dualprevention.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HazardOverdueCheckRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    /** 是否仅标记逾期，默认 true */
    private Boolean markOverdue;
}
