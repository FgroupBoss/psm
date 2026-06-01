package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto;

import lombok.Data;

@Data
public class HotWorkApproverResolveDTO {

    private Long tenantId;
    private String hotWorkLevel;
    private Long areaId;
    private Long unitId;
    private Long permitIssuerUserId;
    private Long supervisorUserId;
    private Long contractorCompanyId;
    private HotWorkWorkflowNodeSnapshotDTO node;
}
