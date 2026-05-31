package com.fgroupboss.ai.psm.risk.inspection.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.ChecklistTemplateRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.ChecklistTemplateVO;

public interface ChecklistTemplateService {

    PageResult<ChecklistTemplateVO> page(Long tenantId, String keyword, int pageNo, int pageSize);

    ChecklistTemplateVO getById(Long tenantId, Long id);

    ChecklistTemplateVO create(ChecklistTemplateRequest request);

    ChecklistTemplateVO update(Long id, ChecklistTemplateRequest request);
}
