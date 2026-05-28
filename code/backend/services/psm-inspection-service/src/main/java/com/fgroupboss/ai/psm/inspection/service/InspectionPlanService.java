package com.fgroupboss.ai.psm.inspection.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.inspection.model.dto.InspectionPlanRequest;
import com.fgroupboss.ai.psm.inspection.model.vo.InspectionPlanVO;

import java.util.List;

public interface InspectionPlanService {

    PageResult<InspectionPlanVO> page(Long tenantId, String keyword, int pageNo, int pageSize);

    InspectionPlanVO getById(Long tenantId, Long id);

    InspectionPlanVO create(InspectionPlanRequest request);

    InspectionPlanVO update(Long id, InspectionPlanRequest request);

    List<InspectionPlanVO> listByMajorHazard(Long tenantId, Long majorHazardId);
}
