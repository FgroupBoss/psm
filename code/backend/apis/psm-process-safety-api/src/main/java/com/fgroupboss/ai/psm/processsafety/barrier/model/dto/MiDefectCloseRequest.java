package com.fgroupboss.ai.psm.processsafety.barrier.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MiDefectCloseRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private String remark;
}
