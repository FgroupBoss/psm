package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HotWorkApprovalProgressVO {

    private Long instanceId;
    private String instanceStatus;
    private Integer currentNodeSeq;
    private Integer totalNodes;
    private String currentNodeName;
    private String signMode;
    private Integer approvedCount;
    private Integer requiredCount;
    private List<HotWorkApprovalNodeProgressVO> nodes = new ArrayList<HotWorkApprovalNodeProgressVO>();
    private List<HotWorkApprovalTaskVO> pendingTasks = new ArrayList<HotWorkApprovalTaskVO>();
}
