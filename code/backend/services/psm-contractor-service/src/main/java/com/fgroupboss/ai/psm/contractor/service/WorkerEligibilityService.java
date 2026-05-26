package com.fgroupboss.ai.psm.contractor.service;

import com.fgroupboss.ai.psm.contractor.model.dto.EligibilityCheckRequest;
import com.fgroupboss.ai.psm.contractor.model.vo.EligibilityCheckResultVO;

public interface WorkerEligibilityService {

    EligibilityCheckResultVO check(EligibilityCheckRequest request);
}
