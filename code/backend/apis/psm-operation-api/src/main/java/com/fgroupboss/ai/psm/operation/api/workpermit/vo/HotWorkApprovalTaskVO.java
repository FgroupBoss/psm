package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

@Data
public class HotWorkApprovalTaskVO {

    private Long id;
    private Long nodeInstanceId;
    private Integer nodeSeq;
    private String nodeName;
    private String signMode;
    private Long assigneeUserId;
    private String assigneeName;
    private String status;
}
