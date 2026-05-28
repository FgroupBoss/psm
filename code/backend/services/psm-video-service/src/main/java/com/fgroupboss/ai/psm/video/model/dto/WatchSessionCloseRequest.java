package com.fgroupboss.ai.psm.video.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WatchSessionCloseRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    private String remark;
}
