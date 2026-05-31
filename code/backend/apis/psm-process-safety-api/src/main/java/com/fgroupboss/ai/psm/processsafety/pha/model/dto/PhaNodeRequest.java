package com.fgroupboss.ai.psm.processsafety.pha.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PhaNodeRequest {
    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "nodeNo is required")
    private String nodeNo;
    @NotBlank(message = "nodeName is required")
    private String nodeName;
    private String designIntent;
    private String parameters;
}
