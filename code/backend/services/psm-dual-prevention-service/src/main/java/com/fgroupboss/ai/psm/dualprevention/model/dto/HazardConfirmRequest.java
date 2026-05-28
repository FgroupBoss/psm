package com.fgroupboss.ai.psm.dualprevention.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class HazardConfirmRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    /** 是否退回（待确认直接退回） */
    private Boolean returned;

    private String hazardLevel;
    private Long assigneeOrgId;
    private Long assigneeUserId;
    private LocalDateTime rectificationDeadline;
    private String content;
}
