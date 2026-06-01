package com.fgroupboss.ai.psm.operation.workpermit.specialty;

import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;

/**
 * 作业票专项类型处理器，供统一编排层按 workType 路由。
 */
public interface WorkPermitSpecialtyHandler {

    boolean supports(WorkPermitEntity permit);

    void applyPreCheck(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint, PreCheckResultVO result);

    void enrichDetail(Long tenantId, WorkPermitEntity permit, WorkPermitDetailVO detail);

    boolean skipGasTestAtSitePermit(WorkPermitEntity permit);
}
