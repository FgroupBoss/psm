package com.fgroupboss.ai.psm.operation.workpermit.client;

import com.fgroupboss.ai.psm.operation.api.contractor.dto.ContractorEligibilityReason;
import com.fgroupboss.ai.psm.operation.api.contractor.dto.ContractorEligibilityRequest;
import com.fgroupboss.ai.psm.operation.api.contractor.dto.ContractorEligibilityResult;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.EligibilityCheckRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.EligibilityCheckResultVO;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.EligibilityReasonVO;
import com.fgroupboss.ai.psm.operation.contractor.service.WorkerEligibilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 人员准入校验客户端 — 同域内直接注入 WorkerEligibilityService，不再 HTTP 调用。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContractorEligibilityClient {

    private final WorkerEligibilityService workerEligibilityService;

    public ContractorEligibilityResult check(ContractorEligibilityRequest request) {
        EligibilityCheckRequest internalReq = toInternalRequest(request);
        EligibilityCheckResultVO internalResult = workerEligibilityService.check(internalReq);
        return toApiResult(internalResult);
    }

    private EligibilityCheckRequest toInternalRequest(ContractorEligibilityRequest req) {
        EligibilityCheckRequest internal = new EligibilityCheckRequest();
        internal.setTenantId(req.getTenantId());
        internal.setCompanyId(req.getCompanyId());
        internal.setWorkerIds(req.getWorkerIds());
        internal.setWorkType(req.getWorkType());
        internal.setCheckPoint(req.getCheckPoint());
        return internal;
    }

    private ContractorEligibilityResult toApiResult(EligibilityCheckResultVO vo) {
        ContractorEligibilityResult result = new ContractorEligibilityResult();
        result.setPassed(vo.isPassed());
        result.setReasons(toApiReasons(vo.getReasons()));
        return result;
    }

    private List<ContractorEligibilityReason> toApiReasons(List<EligibilityReasonVO> reasons) {
        if (reasons == null) {
            return Collections.emptyList();
        }
        return reasons.stream().map(r -> {
            ContractorEligibilityReason apiReason = new ContractorEligibilityReason();
            apiReason.setCode(r.getCode());
            apiReason.setMessage(r.getMessage());
            apiReason.setWorkerId(r.getWorkerId());
            return apiReason;
        }).collect(Collectors.toList());
    }
}
