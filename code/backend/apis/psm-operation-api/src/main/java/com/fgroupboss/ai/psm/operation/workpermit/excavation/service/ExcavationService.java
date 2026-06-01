package com.fgroupboss.ai.psm.operation.workpermit.excavation.service;

import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationCountersignRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationSiteCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationUndergroundFacilityRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationCountersignVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationSiteCheckVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationUndergroundFacilityVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationWorkDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.config.ExcavationCheckPoint;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;

import java.util.List;

public interface ExcavationService {

    ExcavationWorkDetailVO getDetail(Long tenantId, Long permitId);

    ExcavationWorkDetailVO saveDetail(Long tenantId, Long permitId, ExcavationWorkDetailRequest request, String operator);

    List<ExcavationUndergroundFacilityVO> listFacilities(Long tenantId, Long permitId);

    ExcavationUndergroundFacilityVO addFacility(Long tenantId, Long permitId,
                                                ExcavationUndergroundFacilityRequest request, String operator);

    List<ExcavationCountersignVO> listCountersigns(Long tenantId, Long permitId);

    ExcavationCountersignVO addCountersign(Long tenantId, Long permitId,
                                           ExcavationCountersignRequest request, String operator);

    List<ExcavationSiteCheckVO> listSiteChecks(Long tenantId, Long permitId, String stage);

    ExcavationSiteCheckVO addSiteCheck(Long tenantId, Long permitId,
                                       ExcavationSiteCheckRequest request, String operator);

    ExcavationPreCheckResultVO preCheck(Long tenantId, Long permitId, String checkPoint);

    ExcavationFlowProgressVO getFlowProgress(Long tenantId, Long permitId);

    void applyPreCheck(WorkPermitEntity permit, ExcavationCheckPoint checkPoint, PreCheckResultVO result);
}
