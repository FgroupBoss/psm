package com.fgroupboss.ai.psm.processsafety.moc.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class MocImplementationTaskRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    private String taskDesc;
    private Long ownerUserId;
    private LocalDateTime plannedAt;
}
