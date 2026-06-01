package com.fgroupboss.ai.psm.operation.workpermit.specialty;

import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 协调各票种专项处理器，供通用作业票服务调用。
 */
@Component
@RequiredArgsConstructor
public class WorkPermitSpecialtyCoordinator {

    private final List<WorkPermitSpecialtyHandler> handlers;

    public Optional<WorkPermitSpecialtyHandler> findHandler(WorkPermitEntity permit) {
        if (permit == null) {
            return Optional.empty();
        }
        for (WorkPermitSpecialtyHandler handler : handlers) {
            if (handler.supports(permit)) {
                return Optional.of(handler);
            }
        }
        return Optional.empty();
    }

    public void applyPreCheck(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint, PreCheckResultVO result) {
        findHandler(permit).ifPresent(handler -> handler.applyPreCheck(permit, checkPoint, result));
    }

    public void enrichDetail(Long tenantId, WorkPermitEntity permit, WorkPermitDetailVO detail) {
        findHandler(permit).ifPresent(handler -> handler.enrichDetail(tenantId, permit, detail));
    }

    public boolean skipGasTestAtSitePermit(WorkPermitEntity permit) {
        Optional<WorkPermitSpecialtyHandler> handler = findHandler(permit);
        return handler.isPresent() && handler.get().skipGasTestAtSitePermit(permit);
    }
}
