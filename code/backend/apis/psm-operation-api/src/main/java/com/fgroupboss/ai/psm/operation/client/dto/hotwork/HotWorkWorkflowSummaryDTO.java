package com.fgroupboss.ai.psm.operation.client.dto.hotwork;

import lombok.Data;

@Data
public class HotWorkWorkflowSummaryDTO {

    private Long id;
    private String templateCode;
    private String templateName;
    private String hotWorkLevel;
    private String areaScopeType;
    private Integer versionNo;
    private String status;
    private String remark;
}
