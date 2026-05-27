package com.fgroupboss.ai.psm.majorhazard.service;

import com.fgroupboss.ai.psm.majorhazard.model.vo.HazardAlarmSummaryVO;

import java.util.List;

public interface HazardAlarmQueryService {

    List<HazardAlarmSummaryVO> listByHazard(Long tenantId, Long hazardId);
}
