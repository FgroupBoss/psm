package com.fgroupboss.ai.psm.operation.contractor.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.CompanyApproveRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.CompanyReasonRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.ContractorWorkerRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.ContractorWorkerVO;

public interface ContractorWorkerService {

    PageResult<ContractorWorkerVO> page(Long tenantId, Long companyId, String keyword, String accessStatus,
                                        int pageNo, int pageSize);

    ContractorWorkerVO getById(Long tenantId, Long id);

    ContractorWorkerVO create(ContractorWorkerRequest request, String operator);

    ContractorWorkerVO update(Long id, ContractorWorkerRequest request, String operator);

    ContractorWorkerVO submit(Long tenantId, Long id, String operator);

    ContractorWorkerVO approve(Long tenantId, Long id, CompanyApproveRequest request, String operator);

    ContractorWorkerVO suspend(Long tenantId, Long id, CompanyReasonRequest request, String operator);

    ContractorWorkerVO blacklist(Long tenantId, Long id, CompanyReasonRequest request, String operator);

    void refreshCompliance(Long tenantId, Long workerId);
}
