package com.fgroupboss.ai.psm.operation.client.dto.hotwork;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
