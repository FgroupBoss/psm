package com.fgroupboss.ai.psm.risk.majorhazard.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.HazardStatusRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.MajorHazardRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.ResponsibilityReplaceRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.MajorHazardResponsibilityVO;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.MajorHazardVO;

import java.util.List;

public interface MajorHazardService {

    PageResult<MajorHazardVO> page(Long tenantId, String keyword, String status, String level, int pageNo, int pageSize);

    MajorHazardVO getById(Long tenantId, Long id);

    MajorHazardVO create(MajorHazardRequest request, String operator);

    MajorHazardVO update(Long id, MajorHazardRequest request, String operator);

    MajorHazardVO publish(Long tenantId, Long id, String operator);

    MajorHazardVO changeStatus(Long tenantId, Long id, HazardStatusRequest request, String operator);

    List<MajorHazardResponsibilityVO> listResponsibilities(Long tenantId, Long hazardId);

    List<MajorHazardResponsibilityVO> replaceResponsibilities(Long tenantId, Long hazardId,
                                                              ResponsibilityReplaceRequest request, String operator);

    MajorHazardVO bindInspectionPlan(Long tenantId, Long hazardId, Long inspectionPlanId, String operator);
}
