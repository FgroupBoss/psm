package com.fgroupboss.ai.psm.risk.dualprevention.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HazardEscalateRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    private String reason;

    /** 指定通知用户；为空时使用隐患 assigneeUserId。 */
    private Long notifyUserId;
}
