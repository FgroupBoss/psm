package com.fgroupboss.ai.psm.contractor.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorCompanyMapper;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorQualificationMapper;
import com.fgroupboss.ai.psm.contractor.model.dto.ContractorQualificationRequest;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorCompanyEntity;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorQualificationEntity;
import com.fgroupboss.ai.psm.contractor.model.vo.ContractorQualificationVO;
import com.fgroupboss.ai.psm.contractor.service.ContractorQualificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractorQualificationServiceImpl implements ContractorQualificationService {

    private final ContractorQualificationMapper qualificationMapper;
    private final ContractorCompanyMapper companyMapper;

    @Override
    public List<ContractorQualificationVO> listByCompany(Long tenantId, Long companyId) {
        requireCompany(tenantId, companyId);
        List<ContractorQualificationEntity> entities = qualificationMapper.listByCompany(tenantId, companyId);
        List<ContractorQualificationVO> records = new ArrayList<ContractorQualificationVO>();
        for (ContractorQualificationEntity entity : entities) {
            records.add(toVO(entity));
        }
        return records;
    }

    @Override
    public ContractorQualificationVO create(Long companyId, ContractorQualificationRequest request, String operator) {
        requireCompany(request.getTenantId(), companyId);
        ContractorQualificationEntity entity = new ContractorQualificationEntity();
        entity.setTenantId(request.getTenantId());
        entity.setCompanyId(companyId);
        applyRequest(entity, request);
        entity.setStatus("ENABLED");
        entity.setDeleted(0);
        qualificationMapper.insert(entity);
        return toVO(entity);
    }

    @Override
    public ContractorQualificationVO update(Long companyId, Long qualId, ContractorQualificationRequest request,
                                            String operator) {
        ContractorQualificationEntity entity = requireQualification(request.getTenantId(), companyId, qualId);
        applyRequest(entity, request);
        qualificationMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    public void delete(Long tenantId, Long companyId, Long qualId, String operator) {
        ContractorQualificationEntity entity = requireQualification(tenantId, companyId, qualId);
        entity.setDeleted(1);
        qualificationMapper.updateById(entity);
    }

    private void applyRequest(ContractorQualificationEntity entity, ContractorQualificationRequest request) {
        entity.setQualType(request.getQualType().trim());
        entity.setQualName(request.getQualName().trim());
        entity.setQualNo(request.getQualNo());
        entity.setValidFrom(request.getValidFrom());
        entity.setValidTo(request.getValidTo());
        entity.setCoreFlag(Boolean.TRUE.equals(request.getCoreFlag()) ? 1 : 0);
        entity.setFileId(request.getFileId());
    }

    private ContractorQualificationVO toVO(ContractorQualificationEntity entity) {
        ContractorQualificationVO vo = new ContractorQualificationVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setCompanyId(entity.getCompanyId());
        vo.setQualType(entity.getQualType());
        vo.setQualName(entity.getQualName());
        vo.setQualNo(entity.getQualNo());
        vo.setValidFrom(entity.getValidFrom());
        vo.setValidTo(entity.getValidTo());
        vo.setCoreFlag(entity.getCoreFlag());
        vo.setFileId(entity.getFileId());
        vo.setStatus(entity.getStatus());
        boolean expired = isExpired(entity.getValidTo());
        vo.setExpired(expired);
        vo.setCoreExpired(expired && entity.getCoreFlag() != null && entity.getCoreFlag() == 1);
        return vo;
    }

    private boolean isExpired(LocalDate validTo) {
        return validTo != null && validTo.isBefore(LocalDate.now());
    }

    private ContractorCompanyEntity requireCompany(Long tenantId, Long companyId) {
        ContractorCompanyEntity company = companyMapper.selectById(companyId);
        if (company == null || company.getDeleted() != null && company.getDeleted() == 1
                || !tenantId.equals(company.getTenantId())) {
            throw new BusinessException(404, "contractor company not found");
        }
        return company;
    }

    private ContractorQualificationEntity requireQualification(Long tenantId, Long companyId, Long qualId) {
        ContractorQualificationEntity entity = qualificationMapper.selectById(qualId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId()) || !companyId.equals(entity.getCompanyId())) {
            throw new BusinessException(404, "contractor qualification not found");
        }
        return entity;
    }
}
