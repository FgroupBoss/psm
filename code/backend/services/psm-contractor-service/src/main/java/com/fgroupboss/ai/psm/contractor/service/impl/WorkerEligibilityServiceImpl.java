package com.fgroupboss.ai.psm.contractor.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.contractor.config.CompanyStatus;
import com.fgroupboss.ai.psm.contractor.config.EligibilityReasonCode;
import com.fgroupboss.ai.psm.contractor.config.WorkerStatus;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorBlacklistMapper;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorCompanyMapper;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorWorkerMapper;
import com.fgroupboss.ai.psm.contractor.mapper.WorkerCertificateMapper;
import com.fgroupboss.ai.psm.contractor.mapper.WorkerTrainingMapper;
import com.fgroupboss.ai.psm.contractor.model.dto.EligibilityCheckRequest;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorBlacklistEntity;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorCompanyEntity;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorWorkerEntity;
import com.fgroupboss.ai.psm.contractor.model.entity.WorkerCertificateEntity;
import com.fgroupboss.ai.psm.contractor.model.entity.WorkerTrainingRecordEntity;
import com.fgroupboss.ai.psm.contractor.model.vo.EligibilityCheckResultVO;
import com.fgroupboss.ai.psm.contractor.model.vo.EligibilityReasonVO;
import com.fgroupboss.ai.psm.contractor.service.WorkerEligibilityService;
import com.fgroupboss.ai.psm.contractor.service.support.WorkerComplianceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现方式：承载人员准入业务实现，基于 Mapper、远程客户端或支撑组件完成校验、状态流转和结果组装。
 */
@Service
@RequiredArgsConstructor
public class WorkerEligibilityServiceImpl implements WorkerEligibilityService {

    private static final String TARGET_COMPANY = "COMPANY";
    private static final String TARGET_WORKER = "WORKER";

    private final ContractorCompanyMapper companyMapper;
    private final ContractorWorkerMapper workerMapper;
    private final ContractorBlacklistMapper blacklistMapper;
    private final WorkerCertificateMapper certificateMapper;
    private final WorkerTrainingMapper trainingMapper;

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public EligibilityCheckResultVO check(EligibilityCheckRequest request) {
        validateRequest(request);
        List<EligibilityReasonVO> reasons = new ArrayList<EligibilityReasonVO>();
        checkCompany(request, reasons);
        for (Long workerId : request.getWorkerIds()) {
            checkWorker(request.getTenantId(), request.getCompanyId(), workerId, request.getWorkType(), reasons);
        }
        EligibilityCheckResultVO result = new EligibilityCheckResultVO();
        result.setPassed(reasons.isEmpty());
        result.setReasons(reasons);
        return result;
    }

    private void checkCompany(EligibilityCheckRequest request, List<EligibilityReasonVO> reasons) {
        ContractorCompanyEntity company = companyMapper.selectById(request.getCompanyId());
        if (company == null || company.getDeleted() != null && company.getDeleted() == 1
                || !request.getTenantId().equals(company.getTenantId())) {
            reasons.add(reason(EligibilityReasonCode.COMPANY_NOT_APPROVED, "承包商单位不存在或未准入", null));
            return;
        }
        if (CompanyStatus.BLACKLIST.name().equals(company.getStatus())
                || company.getBlacklistFlag() != null && company.getBlacklistFlag() == 1) {
            reasons.add(reason(EligibilityReasonCode.COMPANY_BLACKLIST,
                    "承包商单位「" + company.getCompanyName() + "」已列入黑名单", null));
        } else if (CompanyStatus.SUSPENDED.name().equals(company.getStatus())) {
            reasons.add(reason(EligibilityReasonCode.COMPANY_SUSPENDED,
                    "承包商单位「" + company.getCompanyName() + "」已停权", null));
        } else if (!CompanyStatus.APPROVED.name().equals(company.getStatus())) {
            reasons.add(reason(EligibilityReasonCode.COMPANY_NOT_APPROVED,
                    "承包商单位「" + company.getCompanyName() + "」尚未准入", null));
        }
        ContractorBlacklistEntity blacklist = blacklistMapper.findActiveByTarget(
                request.getTenantId(), TARGET_COMPANY, request.getCompanyId());
        if (blacklist != null && !containsCode(reasons, EligibilityReasonCode.COMPANY_BLACKLIST)) {
            reasons.add(reason(EligibilityReasonCode.COMPANY_BLACKLIST,
                    "承包商单位「" + company.getCompanyName() + "」已列入黑名单", null));
        }
    }

    private void checkWorker(Long tenantId, Long companyId, Long workerId, String workType,
                             List<EligibilityReasonVO> reasons) {
        ContractorWorkerEntity worker = workerMapper.selectById(workerId);
        if (worker == null || worker.getDeleted() != null && worker.getDeleted() == 1
                || !tenantId.equals(worker.getTenantId())) {
            reasons.add(reason(EligibilityReasonCode.WORKER_NOT_FOUND, "人员不存在", workerId));
            return;
        }
        if (!companyId.equals(worker.getCompanyId())) {
            reasons.add(reason(EligibilityReasonCode.WORKER_NOT_FOUND,
                    "人员「" + worker.getName() + "」不属于所选承包商单位", workerId));
            return;
        }
        if (WorkerStatus.BLACKLIST.name().equals(worker.getAccessStatus())) {
            reasons.add(reason(EligibilityReasonCode.WORKER_BLACKLIST,
                    "人员「" + worker.getName() + "」已列入黑名单", workerId));
        } else if (WorkerStatus.SUSPENDED.name().equals(worker.getAccessStatus())) {
            reasons.add(reason(EligibilityReasonCode.WORKER_SUSPENDED,
                    "人员「" + worker.getName() + "」已停权", workerId));
        } else if (WorkerStatus.RESTRICTED.name().equals(worker.getAccessStatus())) {
            reasons.add(reason(EligibilityReasonCode.WORKER_NOT_APPROVED,
                    "人员「" + worker.getName() + "」受限准入（证书或培训失效）", workerId));
        } else if (!WorkerStatus.APPROVED.name().equals(worker.getAccessStatus())) {
            reasons.add(reason(EligibilityReasonCode.WORKER_NOT_APPROVED,
                    "人员「" + worker.getName() + "」尚未准入", workerId));
        }
        ContractorBlacklistEntity blacklist = blacklistMapper.findActiveByTarget(tenantId, TARGET_WORKER, workerId);
        if (blacklist != null && !containsWorkerCode(reasons, EligibilityReasonCode.WORKER_BLACKLIST, workerId)) {
            reasons.add(reason(EligibilityReasonCode.WORKER_BLACKLIST,
                    "人员「" + worker.getName() + "」已列入黑名单", workerId));
        }
        List<WorkerCertificateEntity> certificates = certificateMapper.listByWorker(tenantId, workerId);
        WorkerCertificateEntity expiredCert = WorkerComplianceHelper.firstExpiredCertificate(certificates);
        if (expiredCert != null) {
            reasons.add(reason(EligibilityReasonCode.WORKER_CERT_EXPIRED,
                    "人员「" + worker.getName() + "」证书「" + expiredCert.getCertType() + "」已过期", workerId));
        }
        List<WorkerTrainingRecordEntity> trainings = trainingMapper.listByWorker(tenantId, workerId);
        if (WorkerComplianceHelper.hasInvalidTraining(trainings)) {
            reasons.add(reason(EligibilityReasonCode.WORKER_TRAINING_INVALID,
                    "人员「" + worker.getName() + "」培训不合格或已过期", workerId));
        }
    }

    private void validateRequest(EligibilityCheckRequest request) {
        if (request.getTenantId() == null || request.getTenantId() <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
        if (request.getCompanyId() == null) {
            throw new BusinessException(400, "companyId is required");
        }
        if (request.getWorkerIds() == null || request.getWorkerIds().isEmpty()) {
            throw new BusinessException(400, "workerIds is required");
        }
    }

    private EligibilityReasonVO reason(String code, String message, Long workerId) {
        EligibilityReasonVO vo = new EligibilityReasonVO();
        vo.setCode(code);
        vo.setMessage(message);
        vo.setWorkerId(workerId);
        return vo;
    }

    private boolean containsCode(List<EligibilityReasonVO> reasons, String code) {
        for (EligibilityReasonVO reason : reasons) {
            if (code.equals(reason.getCode()) && reason.getWorkerId() == null) {
                return true;
            }
        }
        return false;
    }

    private boolean containsWorkerCode(List<EligibilityReasonVO> reasons, String code, Long workerId) {
        for (EligibilityReasonVO reason : reasons) {
            if (code.equals(reason.getCode()) && workerId.equals(reason.getWorkerId())) {
                return true;
            }
        }
        return false;
    }
}
