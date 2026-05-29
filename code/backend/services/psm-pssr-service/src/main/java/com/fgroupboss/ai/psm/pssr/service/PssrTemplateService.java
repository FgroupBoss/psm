package com.fgroupboss.ai.psm.pssr.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.pssr.model.dto.PssrTemplateRequest;
import com.fgroupboss.ai.psm.pssr.model.vo.PssrTemplateVO;

/**
 * PSSR 清单模板维护。
 */
public interface PssrTemplateService {

    PageResult<PssrTemplateVO> page(Long tenantId, String keyword, int pageNo, int pageSize);

    PssrTemplateVO getById(Long tenantId, Long id);

    PssrTemplateVO create(PssrTemplateRequest request, String operator);

    PssrTemplateVO update(Long id, PssrTemplateRequest request, String operator);

    void delete(Long tenantId, Long id, String operator);
}
