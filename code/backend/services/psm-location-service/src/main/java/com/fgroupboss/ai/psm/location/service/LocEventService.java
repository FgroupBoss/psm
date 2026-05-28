package com.fgroupboss.ai.psm.location.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.location.model.dto.LocEventIngestRequest;
import com.fgroupboss.ai.psm.location.model.vo.LocEventVO;

import java.time.LocalDateTime;

public interface LocEventService {

    LocEventVO ingest(LocEventIngestRequest request);

    PageResult<LocEventVO> page(Long tenantId, String eventType, String tagNo, Long personId,
                                LocalDateTime fromTime, LocalDateTime toTime, int pageNo, int pageSize);
}
