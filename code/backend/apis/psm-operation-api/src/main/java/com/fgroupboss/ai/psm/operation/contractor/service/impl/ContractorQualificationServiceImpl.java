package com.fgroupboss.ai.psm.operation.contractor.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.operation.contractor.mapper.ContractorCompanyMapper;
import com.fgroupboss.ai.psm.operation.contractor.mapper.ContractorQualificationMapper;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.ContractorQualificationRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.ContractorCompanyEntity;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.ContractorQualificationEntity;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.ContractorQualificationVO;
import com.fgroupboss.ai.psm.operation.contractor.service.ContractorQualificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现方式：承载承包商资质业务实现，基于 Mapper、远程客户端或支撑组件完成校验、状态流转和结果组装。
 */
@Service
@RequiredArgsConstructor
public class ContractorQualificationServiceImpl implements ContractorQualificationService {

    private final ContractorQualificationMapper qualificationMapper;
    private final ContractorCompanyMapper companyMapper;

    /**
     * 实现方式：按承包商企业查询资质记录，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
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

    /**
     * 实现方式：创建业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
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

    /**
     * 实现方式：更新业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public ContractorQualificationVO update(Long companyId, Long qualId, ContractorQualificationRequest request,
                                            String operator) {
        ContractorQualificationEntity entity = requireQualification(request.getTenantId(), companyId, qualId);
        applyRequest(entity, request);
        qualificationMapper.updateById(entity);
        return toVO(entity);
    }

    /**
     * 实现方式：删除业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
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
