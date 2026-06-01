package com.fgroupboss.ai.psm.operation.workpermit.heightwork.support;

import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.config.HeightWorkCheckPoint;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.service.HeightWorkService;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.SpecialtyCheckPoint;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.WorkPermitSpecialtyHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 高处作业专项适配器，接入统一专项协调层。
 */
@Component
@RequiredArgsConstructor
public class HeightWorkSpecialtyHandler implements WorkPermitSpecialtyHandler {

    private final HeightWorkService heightWorkService;

    @Override
    public boolean supports(WorkPermitEntity permit) {
        return heightWorkService.supportsHeightWork(permit);
    }

    @Override
    public void applyPreCheck(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint, PreCheckResultVO result) {
        heightWorkService.applyPreCheck(permit, mapCheckPoint(checkPoint), result);
    }

    @Override
    public void enrichDetail(Long tenantId, WorkPermitEntity permit, WorkPermitDetailVO detail) {
        detail.setHeightWorkDetail(heightWorkService.getDetail(tenantId, permit.getId()));
        detail.setHeightWorkHazardFactors(heightWorkService.listHazardFactors(tenantId, permit.getId()));
        detail.setHeightWorkFlowProgress(heightWorkService.getFlowProgress(tenantId, permit.getId()));
    }

    @Override
    public boolean skipGasTestAtSitePermit(WorkPermitEntity permit) {
        return true;
    }

    private HeightWorkCheckPoint mapCheckPoint(SpecialtyCheckPoint checkPoint) {
        if (checkPoint == SpecialtyCheckPoint.SAVE) {
            return HeightWorkCheckPoint.SAVE;
        }
        if (checkPoint == SpecialtyCheckPoint.SUBMIT) {
            return HeightWorkCheckPoint.SUBMIT;
        }
        if (checkPoint == SpecialtyCheckPoint.SITE_PERMIT) {
            return HeightWorkCheckPoint.SITE_PERMIT;
        }
        if (checkPoint == SpecialtyCheckPoint.RESUME) {
            return HeightWorkCheckPoint.RESUME;
        }
        if (checkPoint == SpecialtyCheckPoint.MONITOR) {
            return HeightWorkCheckPoint.MONITOR;
        }
        return HeightWorkCheckPoint.SUBMIT;
    }
}
