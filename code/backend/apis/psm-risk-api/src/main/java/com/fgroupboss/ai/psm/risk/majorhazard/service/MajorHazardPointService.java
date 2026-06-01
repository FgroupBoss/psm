package com.fgroupboss.ai.psm.risk.majorhazard.service;

import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.HazardPointRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.HazardPointVO;

import java.util.List;

public interface MajorHazardPointService {

    List<HazardPointVO> listPoints(Long tenantId, Long hazardId);

    HazardPointVO bindPoint(Long tenantId, Long hazardId, HazardPointRequest request, String operator);

    void unbindPoint(Long tenantId, Long hazardId, Long relId, String operator);
}
