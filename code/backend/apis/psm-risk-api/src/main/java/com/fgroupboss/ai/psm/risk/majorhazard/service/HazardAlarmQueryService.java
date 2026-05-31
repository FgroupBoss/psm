package com.fgroupboss.ai.psm.risk.majorhazard.service;

import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.HazardAlarmSummaryVO;

import java.util.List;

public interface HazardAlarmQueryService {

    List<HazardAlarmSummaryVO> listByHazard(Long tenantId, Long hazardId);
}
