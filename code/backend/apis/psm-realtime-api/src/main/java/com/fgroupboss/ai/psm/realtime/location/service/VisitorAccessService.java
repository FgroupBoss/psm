package com.fgroupboss.ai.psm.realtime.location.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.realtime.location.model.dto.VisitorRecordIngestRequest;
import com.fgroupboss.ai.psm.realtime.location.model.vo.VisitorAccessRecordVO;

import java.time.LocalDateTime;

public interface VisitorAccessService {

    VisitorAccessRecordVO ingest(VisitorRecordIngestRequest request);

    PageResult<VisitorAccessRecordVO> page(Long tenantId, String visitorName, String gateCode,
                                           LocalDateTime fromTime, LocalDateTime toTime,
                                           int pageNo, int pageSize);
}
