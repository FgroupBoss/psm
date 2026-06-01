package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

@Data
public class HotWorkApprovalNodeProgressVO {

    private Integer nodeSeq;
    private String nodeName;
    private String signMode;
    private String status;
    private Integer approvedCount;
    private Integer requiredCount;
}
