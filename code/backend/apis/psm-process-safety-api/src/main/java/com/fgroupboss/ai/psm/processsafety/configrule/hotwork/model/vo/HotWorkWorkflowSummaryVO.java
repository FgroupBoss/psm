package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo;

import lombok.Data;

@Data
public class HotWorkWorkflowSummaryVO {

    private Long id;
    private String templateCode;
    private String templateName;
    private String hotWorkLevel;
    private String areaScopeType;
    private Integer versionNo;
    private String status;
    private String remark;
}
