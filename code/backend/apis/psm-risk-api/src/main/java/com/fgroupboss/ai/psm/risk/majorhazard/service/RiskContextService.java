package com.fgroupboss.ai.psm.risk.majorhazard.service;

import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.RiskContextRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.RiskContextVO;

public interface RiskContextService {

    RiskContextVO query(RiskContextRequest request);
}
