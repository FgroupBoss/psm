package com.fgroupboss.ai.psm.operation.workpermit.confinedspace.service;

import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ConfinedSpaceDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ConfinedSpaceEntryRecordRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ConfinedSpaceRescuePlanRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpaceDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpaceEntryRecordVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpacePreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpaceRescuePlanVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.SpecialtyCheckPoint;

import java.util.List;

public interface ConfinedSpaceService {

    boolean supports(WorkPermitEntity permit);

    ConfinedSpaceDetailVO getDetail(Long tenantId, Long permitId);

    ConfinedSpaceDetailVO saveDetail(Long tenantId, Long permitId, ConfinedSpaceDetailRequest request, String operator);

    List<ConfinedSpaceEntryRecordVO> listEntryRecords(Long tenantId, Long permitId);

    ConfinedSpaceEntryRecordVO addEntryRecord(Long tenantId, Long permitId,
                                              ConfinedSpaceEntryRecordRequest request, String operator);

    ConfinedSpaceRescuePlanVO getRescuePlan(Long tenantId, Long permitId);

    ConfinedSpaceRescuePlanVO saveRescuePlan(Long tenantId, Long permitId,
                                             ConfinedSpaceRescuePlanRequest request, String operator);

    ConfinedSpacePreCheckResultVO preCheck(Long tenantId, Long permitId, String checkPoint);

    HeightWorkFlowProgressVO getFlowProgress(Long tenantId, Long permitId);

    void applyPreCheck(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint, PreCheckResultVO result);
}
