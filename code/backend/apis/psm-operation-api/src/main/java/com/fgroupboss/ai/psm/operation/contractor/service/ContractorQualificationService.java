package com.fgroupboss.ai.psm.operation.contractor.service;

import com.fgroupboss.ai.psm.operation.contractor.model.dto.ContractorQualificationRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.ContractorQualificationVO;

import java.util.List;

public interface ContractorQualificationService {

    List<ContractorQualificationVO> listByCompany(Long tenantId, Long companyId);

    ContractorQualificationVO create(Long companyId, ContractorQualificationRequest request, String operator);

    ContractorQualificationVO update(Long companyId, Long qualId, ContractorQualificationRequest request, String operator);

    void delete(Long tenantId, Long companyId, Long qualId, String operator);
}
