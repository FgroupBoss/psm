package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo;

import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowNodeSnapshotDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HotWorkApproverResolveResultVO {

    private boolean resolved;
    private String failReason;
    private List<HotWorkApproverVO> approvers = new ArrayList<HotWorkApproverVO>();
    private HotWorkWorkflowNodeSnapshotDTO node;
}
