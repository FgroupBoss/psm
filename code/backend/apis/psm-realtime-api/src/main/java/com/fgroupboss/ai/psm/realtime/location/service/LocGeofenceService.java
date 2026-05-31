package com.fgroupboss.ai.psm.realtime.location.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.realtime.location.model.dto.LocGeofenceRequest;
import com.fgroupboss.ai.psm.realtime.location.model.vo.LocGeofenceVO;

public interface LocGeofenceService {

    PageResult<LocGeofenceVO> page(Long tenantId, String keyword, String fenceType, int pageNo, int pageSize);

    LocGeofenceVO create(LocGeofenceRequest request);
}
