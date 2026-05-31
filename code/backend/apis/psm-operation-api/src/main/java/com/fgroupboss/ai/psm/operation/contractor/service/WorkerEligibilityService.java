package com.fgroupboss.ai.psm.operation.contractor.service;

import com.fgroupboss.ai.psm.operation.contractor.model.dto.EligibilityCheckRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.EligibilityCheckResultVO;

public interface WorkerEligibilityService {

    EligibilityCheckResultVO check(EligibilityCheckRequest request);
}
