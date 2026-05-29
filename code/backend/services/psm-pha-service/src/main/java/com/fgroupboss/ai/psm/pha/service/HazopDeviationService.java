package com.fgroupboss.ai.psm.pha.service;

import com.fgroupboss.ai.psm.pha.model.dto.HazopDeviationRequest;
import com.fgroupboss.ai.psm.pha.model.dto.HazopSubItemRequest;
import com.fgroupboss.ai.psm.pha.model.vo.HazopCauseVO;
import com.fgroupboss.ai.psm.pha.model.vo.HazopConsequenceVO;
import com.fgroupboss.ai.psm.pha.model.vo.HazopDeviationVO;
import com.fgroupboss.ai.psm.pha.model.vo.HazopSafeguardVO;

import java.util.List;

public interface HazopDeviationService {
    List<HazopDeviationVO> listByNode(Long tenantId, Long nodeId);
    HazopDeviationVO create(Long nodeId, HazopDeviationRequest request, String operator);
    List<HazopCauseVO> listCauses(Long tenantId, Long deviationId);
    HazopCauseVO addCause(Long deviationId, HazopSubItemRequest request, String operator);
    List<HazopConsequenceVO> listConsequences(Long tenantId, Long deviationId);
    HazopConsequenceVO addConsequence(Long deviationId, HazopSubItemRequest request, String operator);
    List<HazopSafeguardVO> listSafeguards(Long tenantId, Long deviationId);
    HazopSafeguardVO addSafeguard(Long deviationId, HazopSubItemRequest request, String operator);
}
