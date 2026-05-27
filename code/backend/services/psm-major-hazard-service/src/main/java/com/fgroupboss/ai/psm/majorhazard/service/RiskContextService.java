package com.fgroupboss.ai.psm.majorhazard.service;

import com.fgroupboss.ai.psm.majorhazard.model.dto.RiskContextRequest;
import com.fgroupboss.ai.psm.majorhazard.model.vo.RiskContextVO;

public interface RiskContextService {

    RiskContextVO query(RiskContextRequest request);
}
