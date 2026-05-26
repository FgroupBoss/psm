package com.fgroupboss.ai.psm.contractor.service.impl;

import com.fgroupboss.ai.psm.common.AuditBizType;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.contractor.config.CompanyStatus;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorAuditRecordMapper;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorBlacklistMapper;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorCompanyMapper;
import com.fgroupboss.ai.psm.contractor.model.dto.CompanyApproveRequest;
import com.fgroupboss.ai.psm.contractor.model.dto.CompanyReasonRequest;
import com.fgroupboss.ai.psm.contractor.model.dto.ContractorCompanyRequest;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorAuditRecordEntity;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorBlacklistEntity;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorCompanyEntity;
import com.fgroupboss.ai.psm.contractor.model.vo.ContractorCompanyVO;
import com.fgroupboss.ai.psm.contractor.service.ContractorCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractorCompanyServiceImpl implements ContractorCompanyService {

    private static final String TARGET_COMPANY = "COMPANY";

    private final ContractorCompanyMapper companyMapper;
    private final ContractorAuditRecordMapper auditRecordMapper;
    private final ContractorBlacklistMapper blacklistMapper;

    @Override
    public PageResult<ContractorCompanyVO> page(Long tenantId, String keyword, String status, int pageNo, int pageSize) {
        requireTenantId(tenantId);
        Page page = normalizePage(pageNo, pageSize);
        String normalizedKeyword = normalizeText(keyword);
        String normalizedStatus = normalizeText(status);
        long total = companyMapper.countByTenant(tenantId, normalizedKeyword, normalizedStatus);
        List<ContractorCompanyEntity> entities = total == 0
                ? new ArrayList<ContractorCompanyEntity>()
                : companyMapper.listByTenant(tenantId, normalizedKeyword, normalizedStatus, page.offset, page.pageSize);
        List<ContractorCompanyVO> records = new ArrayList<ContractorCompanyVO>();
        for (ContractorCompanyEntity entity : entities) {
            records.add(toVO(entity));
        }
        return new PageResult<ContractorCompanyVO>(total, page.pageNo, page.pageSize, records);
    }

    @Override
    public ContractorCompanyVO getById(Long tenantId, Long id) {
        return toVO(requireCompany(tenantId, id));
    }

    @Override
    @Transactional
    public ContractorCompanyVO create(ContractorCompanyRequest request, String operator) {
        requireTenantId(request.getTenantId());
        assertCodeUnique(request.getTenantId(), request.getCompanyCode(), null);
        ContractorCompanyEntity entity = new ContractorCompanyEntity();
        entity.setTenantId(request.getTenantId());
        applyRequest(entity, request);
        entity.setStatus(CompanyStatus.DRAFT.name());
        entity.setBlacklistFlag(0);
        entity.setDeleted(0);
        companyMapper.insert(entity);
        writeAudit(request.getTenantId(), entity.getId(), "CREATE", null, entity.getStatus(), null, operator);
        return toVO(entity);
    }

    @Override
    @Transactional
    public ContractorCompanyVO update(Long id, ContractorCompanyRequest request, String operator) {
        ContractorCompanyEntity entity = requireCompany(request.getTenantId(), id);
        CompanyStatus.assertEditable(entity.getStatus());
        assertCodeUnique(request.getTenantId(), request.getCompanyCode(), id);
        String before = entity.getStatus();
        applyRequest(entity, request);
        companyMapper.updateById(entity);
        writeAudit(request.getTenantId(), id, "UPDATE", before, entity.getStatus(), null, operator);
        return toVO(entity);
    }

    @Override
    @Transactional
    public ContractorCompanyVO submit(Long tenantId, Long id, String operator) {
        ContractorCompanyEntity entity = requireCompany(tenantId, id);
        String before = entity.getStatus();
        CompanyStatus.assertSubmit(before);
        String after = CompanyStatus.targetAfterSubmit(before);
        CompanyStatus.assertDirectTransition(before, after);
        entity.setStatus(after);
        companyMapper.updateById(entity);
        writeAudit(tenantId, id, "SUBMIT", before, after, null, operator);
        return toVO(entity);
    }

    @Override
    @Transactional
    public ContractorCompanyVO approve(Long tenantId, Long id, CompanyApproveRequest request, String operator) {
        ContractorCompanyEntity entity = requireCompany(tenantId, id);
        String before = entity.getStatus();
        boolean passed = Boolean.TRUE.equals(request.getPassed());
        CompanyStatus.assertApprove(before, passed);
        String after = CompanyStatus.targetAfterApprove(passed);
        CompanyStatus.assertDirectTransition(before, after);
        entity.setStatus(after);
        companyMapper.updateById(entity);
        writeAudit(tenantId, id, passed ? "APPROVE" : "REJECT", before, after, request.getOpinion(), operator);
        return toVO(entity);
    }

    @Override
    @Transactional
    public ContractorCompanyVO suspend(Long tenantId, Long id, CompanyReasonRequest request, String operator) {
        ContractorCompanyEntity entity = requireCompany(tenantId, id);
        String before = entity.getStatus();
        CompanyStatus.assertSuspend(before);
        String after = CompanyStatus.SUSPENDED.name();
        entity.setStatus(after);
        companyMapper.updateById(entity);
        writeAudit(tenantId, id, "SUSPEND", before, after, request.getReason(), operator);
        return toVO(entity);
    }

    @Override
    @Transactional
    public ContractorCompanyVO blacklist(Long tenantId, Long id, CompanyReasonRequest request, String operator) {
        ContractorCompanyEntity entity = requireCompany(tenantId, id);
        String before = entity.getStatus();
        CompanyStatus.assertBlacklist(before);
        String after = CompanyStatus.BLACKLIST.name();
        entity.setStatus(after);
        entity.setBlacklistFlag(1);
        companyMapper.updateById(entity);
        ContractorBlacklistEntity blacklist = new ContractorBlacklistEntity();
        blacklist.setTenantId(tenantId);
        blacklist.setTargetType(TARGET_COMPANY);
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

    private void applyRequest(ContractorCompanyEntity entity, ContractorCompanyRequest request) {
        entity.setCompanyCode(request.getCompanyCode().trim());
        entity.setCompanyName(request.getCompanyName().trim());
        entity.setContactName(request.getContactName());
        entity.setContactPhone(request.getContactPhone());
        entity.setBusinessScope(request.getBusinessScope());
        entity.setRemark(request.getRemark());
    }

    private void assertCodeUnique(Long tenantId, String companyCode, Long excludeId) {
        ContractorCompanyEntity existing = companyMapper.findByCode(tenantId, companyCode.trim());
        if (existing != null && (excludeId == null || !excludeId.equals(existing.getId()))) {
            throw new BusinessException(400, "company code already exists: " + companyCode);
        }
    }

    private ContractorCompanyEntity requireCompany(Long tenantId, Long id) {
        requireTenantId(tenantId);
        ContractorCompanyEntity entity = companyMapper.selectById(id);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "contractor company not found");
        }
        return entity;
    }

    private void writeAudit(Long tenantId, Long companyId, String action, String beforeStatus, String afterStatus,
                            String opinion, String operator) {
        ContractorAuditRecordEntity record = new ContractorAuditRecordEntity();
        record.setTenantId(tenantId);
        record.setTargetType(AuditBizType.CONTRACTOR_COMPANY.name());
        record.setTargetId(companyId);
        record.setAction(action);
        record.setBeforeStatus(beforeStatus);
        record.setAfterStatus(afterStatus);
        record.setOpinion(opinion);
        record.setOperatorName(defaultOperator(operator));
        record.setOperatedAt(LocalDateTime.now());
        auditRecordMapper.insert(record);
    }

    private ContractorCompanyVO toVO(ContractorCompanyEntity entity) {
        ContractorCompanyVO vo = new ContractorCompanyVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setCompanyCode(entity.getCompanyCode());
        vo.setCompanyName(entity.getCompanyName());
        vo.setContactName(entity.getContactName());
        vo.setContactPhone(entity.getContactPhone());
        vo.setBusinessScope(entity.getBusinessScope());
        vo.setStatus(entity.getStatus());
        vo.setBlacklistFlag(entity.getBlacklistFlag());
        vo.setRemark(entity.getRemark());
        return vo;
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
