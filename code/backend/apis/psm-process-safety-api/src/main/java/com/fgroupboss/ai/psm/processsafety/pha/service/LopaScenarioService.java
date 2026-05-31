package com.fgroupboss.ai.psm.processsafety.pha.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.LopaScenarioRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.LopaCalculateResultVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.LopaScenarioVO;

public interface LopaScenarioService {
    PageResult<LopaScenarioVO> page(Long tenantId, Long projectId, int pageNo, int pageSize);
    LopaScenarioVO create(LopaScenarioRequest request, String operator);
    LopaCalculateResultVO calculate(Long id, Long tenantId);
}
