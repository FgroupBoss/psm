package com.fgroupboss.ai.psm.operation.workpermit.specialty;

import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowNodeVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.workpermit.config.WorkPermitStatus;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 构建专项流程进度视图，复用 {@link HeightWorkFlowProgressVO} 作为通用进度载体。
 */
@Component
public class SpecialtyFlowProgressBuilder {

    public HeightWorkFlowProgressVO build(WorkPermitEntity permit, String specialtyLevel, Date validUntil) {
        HeightWorkFlowProgressVO vo = new HeightWorkFlowProgressVO();
        vo.setPermitStatus(permit.getStatus());
        vo.setHeightLevel(specialtyLevel);
        vo.setValidUntil(validUntil);
        vo.setNodes(buildFlowNodes(permit));
        return vo;
    }

    private List<HeightWorkFlowNodeVO> buildFlowNodes(WorkPermitEntity permit) {
        String status = permit.getStatus();
        List<HeightWorkFlowNodeVO> nodes = new ArrayList<HeightWorkFlowNodeVO>();
        nodes.add(flowNode("APPLY", "申请", status, WorkPermitStatus.DRAFT.name()));
        nodes.add(flowNode("CLASSIFY", "分级分类", status, WorkPermitStatus.DRAFT.name()));
        nodes.add(flowNode("JSA", "安全分析及交底", status, WorkPermitStatus.DRAFT.name()));
        nodes.add(flowNode("DISCLOSURE", "安全交底签字", status, WorkPermitStatus.DRAFT.name()));
        nodes.add(flowNode("APPROVING", "审批中", status, WorkPermitStatus.APPROVING.name()));
        nodes.add(flowNode("PENDING", "待作业", status, WorkPermitStatus.PENDING_SITE_PERMIT.name()));
        nodes.add(flowNode("IN_PROGRESS", "作业中", status, WorkPermitStatus.IN_PROGRESS.name()));
        nodes.add(flowNode("SUSPENDED", "暂停中", status, WorkPermitStatus.SUSPENDED.name()));
        nodes.add(flowNode("ACCEPTANCE", "待验收", status, WorkPermitStatus.PENDING_ACCEPTANCE.name()));
        nodes.add(flowNode("CLOSURE", "待关闭", status, WorkPermitStatus.PENDING_ACCEPTANCE.name()));
        nodes.add(flowNode("CLOSED", "作业关闭", status, WorkPermitStatus.CLOSED.name()));
        return nodes;
    }

    private HeightWorkFlowNodeVO flowNode(String code, String name, String currentStatus, String matchStatus) {
        HeightWorkFlowNodeVO node = new HeightWorkFlowNodeVO();
        node.setNodeCode(code);
        node.setNodeName(name);
        node.setCurrent(matchStatus.equals(currentStatus));
        node.setStatus(node.isCurrent() ? "CURRENT" : "PENDING");
        return node;
    }
}
