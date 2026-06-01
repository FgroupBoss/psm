package com.fgroupboss.ai.psm.risk.inspection.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.InspectionRouteRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.InspectionRouteVO;

public interface InspectionRouteService {

    PageResult<InspectionRouteVO> page(Long tenantId, String keyword, int pageNo, int pageSize);

    InspectionRouteVO getById(Long tenantId, Long id);

    InspectionRouteVO create(InspectionRouteRequest request);

    InspectionRouteVO update(Long id, InspectionRouteRequest request);
}
