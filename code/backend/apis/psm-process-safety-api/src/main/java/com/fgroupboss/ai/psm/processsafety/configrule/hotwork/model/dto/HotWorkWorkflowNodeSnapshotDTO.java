package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto;

import lombok.Data;

@Data
public class HotWorkWorkflowNodeSnapshotDTO {

    private Integer nodeSeq;
    private String nodeName;
    private String signMode;
    private String approverRuleType;
    private String approverRuleValue;
    private Integer timeoutHours;
    private Boolean required;
}
