package com.fgroupboss.ai.psm.operation.workpermit.lifting.service;

import com.fgroupboss.ai.psm.operation.api.workpermit.dto.LiftingEquipmentCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.LiftingTrialRecordRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.LiftingWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingEquipmentCheckVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingTrialRecordVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingWorkDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.SpecialtyCheckPoint;

import java.util.List;

public interface LiftingService {

    boolean supports(WorkPermitEntity permit);

    LiftingWorkDetailVO getDetail(Long tenantId, Long permitId);

    LiftingWorkDetailVO saveDetail(Long tenantId, Long permitId, LiftingWorkDetailRequest request, String operator);

    List<LiftingEquipmentCheckVO> listEquipmentChecks(Long tenantId, Long permitId);

    LiftingEquipmentCheckVO addEquipmentCheck(Long tenantId, Long permitId,
                                              LiftingEquipmentCheckRequest request, String operator);

    List<LiftingTrialRecordVO> listTrialRecords(Long tenantId, Long permitId);

    LiftingTrialRecordVO addTrialRecord(Long tenantId, Long permitId,
                                        LiftingTrialRecordRequest request, String operator);

    LiftingPreCheckResultVO preCheck(Long tenantId, Long permitId, String checkPoint);

    HeightWorkFlowProgressVO getFlowProgress(Long tenantId, Long permitId);

    void applyPreCheck(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint, PreCheckResultVO result);
}
