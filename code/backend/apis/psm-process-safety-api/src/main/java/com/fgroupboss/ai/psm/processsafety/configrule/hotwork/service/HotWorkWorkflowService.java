package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.service;

import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkApproverResolveDTO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowQueryDTO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowSnapshotDTO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowTemplateRequest;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo.HotWorkApproverResolveResultVO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo.HotWorkWorkflowDetailVO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo.HotWorkWorkflowSummaryVO;

import java.util.List;

public interface HotWorkWorkflowService {

    HotWorkWorkflowDetailVO create(HotWorkWorkflowTemplateRequest request, String operator);

    HotWorkWorkflowDetailVO update(Long id, HotWorkWorkflowTemplateRequest request, String operator);

    HotWorkWorkflowDetailVO get(Long tenantId, Long id);

    List<HotWorkWorkflowSummaryVO> listAvailable(HotWorkWorkflowQueryDTO query);

    HotWorkWorkflowSnapshotDTO getSnapshot(Long tenantId, Long templateId, Integer versionNo);

    HotWorkApproverResolveResultVO resolveApprovers(HotWorkApproverResolveDTO request);

    void publish(Long tenantId, Long id, String operator);

    void disable(Long tenantId, Long id, String operator);
}
