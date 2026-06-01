package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class HotWorkWorkflowNodeRequest {

    @NotNull(message = "nodeSeq is required")
    private Integer nodeSeq;
    @NotBlank(message = "nodeName is required")
    private String nodeName;
    @NotBlank(message = "signMode is required")
    private String signMode;
    @NotBlank(message = "approverRuleType is required")
    private String approverRuleType;
    @NotBlank(message = "approverRuleValue is required")
    private String approverRuleValue;
    private Integer timeoutHours = 0;
    private Boolean required = Boolean.TRUE;
}
