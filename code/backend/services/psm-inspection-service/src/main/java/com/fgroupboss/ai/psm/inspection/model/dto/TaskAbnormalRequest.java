package com.fgroupboss.ai.psm.inspection.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TaskAbnormalRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private Long taskItemId;
    private Long routePointId;
    @NotBlank(message = "abnormalDesc is required")
    private String abnormalDesc;
    @NotBlank(message = "photoUrls is required")
    private String photoUrls;
    private String severity;
    /** 是否同步创建隐患草稿，默认 true */
    private Boolean createHazard = Boolean.TRUE;
}
