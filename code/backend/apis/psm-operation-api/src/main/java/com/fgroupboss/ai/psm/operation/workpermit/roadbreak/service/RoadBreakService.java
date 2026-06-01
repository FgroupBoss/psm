package com.fgroupboss.ai.psm.operation.workpermit.roadbreak.service;

import com.fgroupboss.ai.psm.operation.api.workpermit.dto.RoadBreakDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.RoadBreakSiteControlRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.RoadBreakTrafficPlanRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakSiteControlVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakTrafficPlanVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.roadbreak.config.RoadBreakCheckPoint;

import java.util.List;

public interface RoadBreakService {

    RoadBreakDetailVO getDetail(Long tenantId, Long permitId);

    RoadBreakDetailVO saveDetail(Long tenantId, Long permitId, RoadBreakDetailRequest request, String operator);

    RoadBreakTrafficPlanVO getTrafficPlan(Long tenantId, Long permitId);

    RoadBreakTrafficPlanVO saveTrafficPlan(Long tenantId, Long permitId,
                                           RoadBreakTrafficPlanRequest request, String operator);

    List<RoadBreakSiteControlVO> listSiteControls(Long tenantId, Long permitId);

    RoadBreakSiteControlVO addSiteControl(Long tenantId, Long permitId,
                                          RoadBreakSiteControlRequest request, String operator);

    RoadBreakPreCheckResultVO preCheck(Long tenantId, Long permitId, String checkPoint);

    RoadBreakFlowProgressVO getFlowProgress(Long tenantId, Long permitId);

    void applyPreCheck(WorkPermitEntity permit, RoadBreakCheckPoint checkPoint, PreCheckResultVO result);
}
