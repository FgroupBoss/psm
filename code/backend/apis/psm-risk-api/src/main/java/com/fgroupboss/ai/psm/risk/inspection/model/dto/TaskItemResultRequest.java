package com.fgroupboss.ai.psm.risk.inspection.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TaskItemResultRequest {

    @NotNull(message = "checklistItemId is required")
    private Long checklistItemId;
    private Long routePointId;
    private String resultValue;
    @NotBlank(message = "resultStatus is required")
    private String resultStatus;
    private String photoUrls;
    private String remark;
}
