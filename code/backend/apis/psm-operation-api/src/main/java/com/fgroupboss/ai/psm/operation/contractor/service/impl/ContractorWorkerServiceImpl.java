package com.fgroupboss.ai.psm.operation.contractor.service.impl;

import com.fgroupboss.ai.psm.common.AuditBizType;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.audit.CentralAuditClient;
import com.fgroupboss.ai.psm.operation.contractor.config.WorkerStatus;
import com.fgroupboss.ai.psm.operation.contractor.mapper.ContractorAuditRecordMapper;
import com.fgroupboss.ai.psm.operation.contractor.mapper.ContractorBlacklistMapper;
import com.fgroupboss.ai.psm.operation.contractor.mapper.ContractorCompanyMapper;
import com.fgroupboss.ai.psm.operation.contractor.mapper.ContractorWorkerMapper;
import com.fgroupboss.ai.psm.operation.contractor.mapper.WorkerCertificateMapper;
import com.fgroupboss.ai.psm.operation.contractor.mapper.WorkerTrainingMapper;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.CompanyApproveRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.CompanyReasonRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.ContractorWorkerRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.ContractorAuditRecordEntity;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.ContractorBlacklistEntity;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.ContractorCompanyEntity;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.ContractorWorkerEntity;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.WorkerCertificateEntity;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.WorkerTrainingRecordEntity;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.ContractorWorkerVO;
import com.fgroupboss.ai.psm.operation.contractor.service.ContractorWorkerService;
import com.fgroupboss.ai.psm.operation.contractor.service.support.WorkerComplianceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现方式：承载承包商人员业务实现，基于 Mapper、远程客户端或支撑组件完成校验、状态流转和结果组装。
 */
@Service
@RequiredArgsConstructor
public class ContractorWorkerServiceImpl implements ContractorWorkerService {

    private static final String TARGET_WORKER = "WORKER";

    private final ContractorWorkerMapper workerMapper;
    private final ContractorCompanyMapper companyMapper;
    private final WorkerCertificateMapper certificateMapper;
    private final WorkerTrainingMapper trainingMapper;
    private final ContractorAuditRecordMapper auditRecordMapper;
    private final ContractorBlacklistMapper blacklistMapper;
    private final CentralAuditClient centralAuditClient;

    /**
     * 实现方式：分页查询业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public PageResult<ContractorWorkerVO> page(Long tenantId, Long companyId, String keyword, String accessStatus,
                                               int pageNo, int pageSize) {
        requireTenantId(tenantId);
        Page page = normalizePage(pageNo, pageSize);
        String normalizedKeyword = normalizeText(keyword);
        String normalizedAccessStatus = normalizeText(accessStatus);
        long total = workerMapper.countByTenant(tenantId, companyId, normalizedKeyword, normalizedAccessStatus);
        List<ContractorWorkerEntity> entities = total == 0
                ? new ArrayList<ContractorWorkerEntity>()
                : workerMapper.listByTenant(tenantId, companyId, normalizedKeyword, normalizedAccessStatus,
                page.offset, page.pageSize);
        List<ContractorWorkerVO> records = new ArrayList<ContractorWorkerVO>();
        for (ContractorWorkerEntity entity : entities) {
            records.add(toVO(entity));
        }
        return new PageResult<ContractorWorkerVO>(total, page.pageNo, page.pageSize, records);
    }

    /**
     * 实现方式：按 ID 查询详情，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public ContractorWorkerVO getById(Long tenantId, Long id) {
        return toVO(requireWorker(tenantId, id));
    }

    /**
     * 实现方式：创建业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public ContractorWorkerVO create(ContractorWorkerRequest request, String operator) {
        requireTenantId(request.getTenantId());
        requireCompany(request.getTenantId(), request.getCompanyId());
        assertCodeUnique(request.getTenantId(), request.getWorkerCode(), null);
        ContractorWorkerEntity entity = new ContractorWorkerEntity();
        entity.setTenantId(request.getTenantId());
        entity.setCompanyId(request.getCompanyId());
        applyRequest(entity, request);
        entity.setAccessStatus(WorkerStatus.INCOMPLETE.name());
        entity.setCertificateStatus("MISSING");
        entity.setTrainingStatus("MISSING");
        entity.setStatus("ENABLED");
        entity.setDeleted(0);
        workerMapper.insert(entity);
        writeAudit(request.getTenantId(), entity.getId(), "CREATE", null, entity.getAccessStatus(), null, operator);
        return toVO(entity);
    }

    /**
     * 实现方式：更新业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public ContractorWorkerVO update(Long id, ContractorWorkerRequest request, String operator) {
        ContractorWorkerEntity entity = requireWorker(request.getTenantId(), id);
        WorkerStatus.assertEditable(entity.getAccessStatus());
        assertCodeUnique(request.getTenantId(), request.getWorkerCode(), id);
        requireCompany(request.getTenantId(), request.getCompanyId());
        String before = entity.getAccessStatus();
        applyRequest(entity, request);
        refreshAndPersistCompliance(entity);
        workerMapper.updateById(entity);
        writeAudit(request.getTenantId(), id, "UPDATE", before, entity.getAccessStatus(), null, operator);
        return toVO(entity);
    }

    /**
     * 实现方式：提交审批，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public ContractorWorkerVO submit(Long tenantId, Long id, String operator) {
        ContractorWorkerEntity entity = requireWorker(tenantId, id);
        String before = entity.getAccessStatus();
        WorkerStatus.assertSubmit(before);
        String after = WorkerStatus.targetAfterSubmit(before);
        WorkerStatus.assertDirectTransition(before, after);
        entity.setAccessStatus(after);
        workerMapper.updateById(entity);
        writeAudit(tenantId, id, "SUBMIT", before, after, null, operator);
        return toVO(entity);
    }

    /**
     * 实现方式：审批业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public ContractorWorkerVO approve(Long tenantId, Long id, CompanyApproveRequest request, String operator) {
        ContractorWorkerEntity entity = requireWorker(tenantId, id);
        String before = entity.getAccessStatus();
        boolean passed = Boolean.TRUE.equals(request.getPassed());
        WorkerStatus.assertApprove(before, passed);
        String after = WorkerStatus.targetAfterApprove(passed);
        WorkerStatus.assertDirectTransition(before, after);
        entity.setAccessStatus(after);
        workerMapper.updateById(entity);
        writeAudit(tenantId, id, passed ? "APPROVE" : "REJECT", before, after, request.getOpinion(), operator);
        return toVO(entity);
    }

    /**
     * 实现方式：挂起业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public ContractorWorkerVO suspend(Long tenantId, Long id, CompanyReasonRequest request, String operator) {
        ContractorWorkerEntity entity = requireWorker(tenantId, id);
        String before = entity.getAccessStatus();
        WorkerStatus.assertSuspend(before);
        String after = WorkerStatus.SUSPENDED.name();
        entity.setAccessStatus(after);
        workerMapper.updateById(entity);
        writeAudit(tenantId, id, "SUSPEND", before, after, request.getReason(), operator);
        return toVO(entity);
    }

    /**
     * 实现方式：加入黑名单，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public ContractorWorkerVO blacklist(Long tenantId, Long id, CompanyReasonRequest request, String operator) {
        ContractorWorkerEntity entity = requireWorker(tenantId, id);
        String before = entity.getAccessStatus();
        WorkerStatus.assertBlacklist(before);
        String after = WorkerStatus.BLACKLIST.name();
        entity.setAccessStatus(after);
        workerMapper.updateById(entity);
        ContractorBlacklistEntity blacklist = new ContractorBlacklistEntity();
        blacklist.setTenantId(tenantId);
        blacklist.setTargetType(TARGET_WORKER);
        blacklist.setTargetId(id);
        blacklist.setReason(request.getReason().trim());
        blacklist.setEffectiveAt(LocalDateTime.now());
        blacklist.setStatus("ACTIVE");
        blacklist.setOperatorName(operator);
        blacklist.setDeleted(0);
        blacklistMapper.insert(blacklist);
        writeAudit(tenantId, id, "BLACKLIST", before, after, request.getReason(), operator);
        return toVO(entity);
    }

    /**
     * 实现方式：刷新人员合规状态，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void refreshCompliance(Long tenantId, Long workerId) {
        ContractorWorkerEntity entity = requireWorker(tenantId, workerId);
        refreshAndPersistCompliance(entity);
        workerMapper.updateById(entity);
    }

    private void applyRequest(ContractorWorkerEntity entity, ContractorWorkerRequest request) {
        entity.setCompanyId(request.getCompanyId());
        entity.setWorkerCode(request.getWorkerCode().trim());
        entity.setName(request.getName().trim());
        entity.setPhoneMasked(request.getPhoneMasked());
        entity.setTradeType(request.getTradeType());
        entity.setGateCardNo(request.getGateCardNo());
        entity.setLocationTagNo(request.getLocationTagNo());
    }

    private void refreshAndPersistCompliance(ContractorWorkerEntity entity) {
        List<WorkerCertificateEntity> certificates = certificateMapper.listByWorker(entity.getTenantId(), entity.getId());
        List<WorkerTrainingRecordEntity> trainings = trainingMapper.listByWorker(entity.getTenantId(), entity.getId());
        entity.setCertificateStatus(WorkerComplianceHelper.computeCertificateStatus(certificates));
        entity.setTrainingStatus(WorkerComplianceHelper.computeTrainingStatus(trainings));
        maybeMarkRestricted(entity, certificates, trainings);
    }

    private void maybeMarkRestricted(ContractorWorkerEntity entity, List<WorkerCertificateEntity> certificates,
                                     List<WorkerTrainingRecordEntity> trainings) {
        if (!WorkerStatus.APPROVED.name().equals(entity.getAccessStatus())) {
            return;
        }
        if (WorkerComplianceHelper.hasExpiredCertificate(certificates)
                || WorkerComplianceHelper.hasInvalidTraining(trainings)) {
            entity.setAccessStatus(WorkerStatus.RESTRICTED.name());
        }
    }

    private ContractorWorkerVO toVO(ContractorWorkerEntity entity) {
        List<WorkerCertificateEntity> certificates = certificateMapper.listByWorker(entity.getTenantId(), entity.getId());
        List<WorkerTrainingRecordEntity> trainings = trainingMapper.listByWorker(entity.getTenantId(), entity.getId());
        ContractorWorkerVO vo = new ContractorWorkerVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setCompanyId(entity.getCompanyId());
        vo.setWorkerCode(entity.getWorkerCode());
        vo.setName(entity.getName());
        vo.setPhoneMasked(entity.getPhoneMasked());
        vo.setTradeType(entity.getTradeType());
        vo.setAccessStatus(entity.getAccessStatus());
        vo.setTrainingStatus(WorkerComplianceHelper.computeTrainingStatus(trainings));
        vo.setCertificateStatus(WorkerComplianceHelper.computeCertificateStatus(certificates));
        vo.setGateCardNo(entity.getGateCardNo());
        vo.setLocationTagNo(entity.getLocationTagNo());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    private void assertCodeUnique(Long tenantId, String workerCode, Long excludeId) {
        ContractorWorkerEntity existing = workerMapper.findByCode(tenantId, workerCode.trim());
        if (existing != null && (excludeId == null || !excludeId.equals(existing.getId()))) {
            throw new BusinessException(400, "worker code already exists: " + workerCode);
        }
    }

    private ContractorWorkerEntity requireWorker(Long tenantId, Long id) {
        requireTenantId(tenantId);
        ContractorWorkerEntity entity = workerMapper.selectById(id);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "contractor worker not found");
        }
        return entity;
    }

    private ContractorCompanyEntity requireCompany(Long tenantId, Long companyId) {
        ContractorCompanyEntity company = companyMapper.selectById(companyId);
        if (company == null || company.getDeleted() != null && company.getDeleted() == 1
                || !tenantId.equals(company.getTenantId())) {
            throw new BusinessException(404, "contractor company not found");
        }
        return company;
    }

    private void writeAudit(Long tenantId, Long workerId, String action, String beforeStatus, String afterStatus,
                            String opinion, String operator) {
        ContractorAuditRecordEntity record = new ContractorAuditRecordEntity();
        record.setTenantId(tenantId);
        record.setTargetType(AuditBizType.CONTRACTOR_WORKER.name());
        record.setTargetId(workerId);
        record.setAction(action);
        record.setBeforeStatus(beforeStatus);
        record.setAfterStatus(afterStatus);
        record.setOpinion(opinion);
        record.setOperatorName(defaultOperator(operator));
        record.setOperatedAt(LocalDateTime.now());
        auditRecordMapper.insert(record);
        centralAuditClient.append(CentralAuditClient.build(tenantId, record.getOperatorName(), action,
                AuditBizType.CONTRACTOR_WORKER.name(), workerId, beforeStatus, afterStatus));
    }

    private String defaultOperator(String operator) {
        return StringUtils.hasText(operator) ? operator.trim() : "system";
    }

    private void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private Page normalizePage(int pageNo, int pageSize) {
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);
        return new Page(normalizedPageNo, normalizedPageSize);
    }

    private static final class Page {
        private final int pageNo;
        private final int pageSize;
        private final int offset;

        private Page(int pageNo, int pageSize) {
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.offset = (pageNo - 1) * pageSize;
        }
    }
}
