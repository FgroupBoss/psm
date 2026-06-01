package com.fgroupboss.ai.psm.risk.inspection.model.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TaskItemSubmitRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotEmpty(message = "items is required")
    @Valid
    private List<TaskItemResultRequest> items;
}
