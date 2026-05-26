package com.fgroupboss.ai.psm.contractor.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.contractor.model.dto.CompanyApproveRequest;
import com.fgroupboss.ai.psm.contractor.model.dto.CompanyReasonRequest;
import com.fgroupboss.ai.psm.contractor.model.dto.ContractorCompanyRequest;
import com.fgroupboss.ai.psm.contractor.model.vo.ContractorCompanyVO;

public interface ContractorCompanyService {

    PageResult<ContractorCompanyVO> page(Long tenantId, String keyword, String status, int pageNo, int pageSize);

    ContractorCompanyVO getById(Long tenantId, Long id);

    ContractorCompanyVO create(ContractorCompanyRequest request, String operator);

    ContractorCompanyVO update(Long id, ContractorCompanyRequest request, String operator);

    ContractorCompanyVO submit(Long tenantId, Long id, String operator);

    ContractorCompanyVO approve(Long tenantId, Long id, CompanyApproveRequest request, String operator);

    ContractorCompanyVO suspend(Long tenantId, Long id, CompanyReasonRequest request, String operator);

    ContractorCompanyVO blacklist(Long tenantId, Long id, CompanyReasonRequest request, String operator);
}
