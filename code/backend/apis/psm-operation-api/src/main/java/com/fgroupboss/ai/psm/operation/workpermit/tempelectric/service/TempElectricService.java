package com.fgroupboss.ai.psm.operation.workpermit.tempelectric.service;

import com.fgroupboss.ai.psm.operation.api.workpermit.dto.TempElectricDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.TempElectricFacilityRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.TempElectricInspectionRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricFacilityVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricInspectionVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.SpecialtyCheckPoint;

import java.util.List;

public interface TempElectricService {

    boolean supports(WorkPermitEntity permit);

    TempElectricDetailVO getDetail(Long tenantId, Long permitId);

    TempElectricDetailVO saveDetail(Long tenantId, Long permitId, TempElectricDetailRequest request, String operator);

    List<TempElectricFacilityVO> listFacilities(Long tenantId, Long permitId);

    TempElectricFacilityVO addFacility(Long tenantId, Long permitId,
                                       TempElectricFacilityRequest request, String operator);

    List<TempElectricInspectionVO> listInspections(Long tenantId, Long permitId);

    TempElectricInspectionVO addInspection(Long tenantId, Long permitId,
                                           TempElectricInspectionRequest request, String operator);

    TempElectricPreCheckResultVO preCheck(Long tenantId, Long permitId, String checkPoint);

    HeightWorkFlowProgressVO getFlowProgress(Long tenantId, Long permitId);

    void applyPreCheck(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint, PreCheckResultVO result);
}
