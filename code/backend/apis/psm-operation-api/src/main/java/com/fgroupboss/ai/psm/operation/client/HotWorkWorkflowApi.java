package com.fgroupboss.ai.psm.operation.client;

import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkApproverResolveDTO;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkApproverResolveResultDTO;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkWorkflowQueryDTO;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkWorkflowSnapshotDTO;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkWorkflowSummaryDTO;

import java.util.List;

/**
 * 动火审批流配置域契约（operation → process-safety）。
 */
public interface HotWorkWorkflowApi {

    List<HotWorkWorkflowSummaryDTO> listAvailable(HotWorkWorkflowQueryDTO query);

    HotWorkWorkflowSnapshotDTO getSnapshot(Long tenantId, Long templateId, Integer versionNo);

    HotWorkApproverResolveResultDTO resolveApprovers(HotWorkApproverResolveDTO request);
}
