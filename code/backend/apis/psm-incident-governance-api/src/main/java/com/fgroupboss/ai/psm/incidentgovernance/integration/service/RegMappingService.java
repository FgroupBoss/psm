package com.fgroupboss.ai.psm.incidentgovernance.integration.service;

import com.fgroupboss.ai.psm.incidentgovernance.integration.model.dto.RegMappingSaveRequest;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegMappingVO;

public interface RegMappingService {

    RegMappingVO list(Long tenantId, String platformCode, String dataDomain);

    RegMappingVO save(RegMappingSaveRequest request);
}
