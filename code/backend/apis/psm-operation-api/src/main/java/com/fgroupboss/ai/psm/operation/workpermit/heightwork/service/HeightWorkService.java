package com.fgroupboss.ai.psm.operation.workpermit.heightwork.service;

import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkEnvironmentCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkHazardFactorRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkProtectionCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkEnvironmentCheckVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkHazardFactorVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkProtectionCheckVO;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.config.HeightWorkCheckPoint;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;

import java.util.List;

public interface HeightWorkService {

    boolean supportsHeightWork(WorkPermitEntity permit);

    HeightWorkDetailVO getDetail(Long tenantId, Long permitId);

    HeightWorkDetailVO saveDetail(Long tenantId, Long permitId, HeightWorkDetailRequest request, String operator);

    List<HeightWorkHazardFactorVO> listHazardFactors(Long tenantId, Long permitId);

    HeightWorkHazardFactorVO addHazardFactor(Long tenantId, Long permitId, HeightWorkHazardFactorRequest request, String operator);

    HeightWorkHazardFactorVO confirmHazardFactor(Long tenantId, Long permitId, Long factorId, String operator);

    List<HeightWorkProtectionCheckVO> listProtectionChecks(Long tenantId, Long permitId, String checkStage);

    HeightWorkProtectionCheckVO addProtectionCheck(Long tenantId, Long permitId,
                                                   HeightWorkProtectionCheckRequest request, String operator);

    List<HeightWorkEnvironmentCheckVO> listEnvironmentChecks(Long tenantId, Long permitId, String checkStage);

    HeightWorkEnvironmentCheckVO addEnvironmentCheck(Long tenantId, Long permitId,
                                                     HeightWorkEnvironmentCheckRequest request, String operator);

    HeightWorkDetailVO recalculate(Long tenantId, Long permitId);

    HeightWorkPreCheckResultVO preCheck(Long tenantId, Long permitId, String checkPoint);

    HeightWorkFlowProgressVO getFlowProgress(Long tenantId, Long permitId);

    void applyPreCheck(WorkPermitEntity permit, HeightWorkCheckPoint checkPoint, PreCheckResultVO result);
}
