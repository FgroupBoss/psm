package com.fgroupboss.ai.psm.realtime.location.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.realtime.location.model.dto.GateRecordIngestRequest;
import com.fgroupboss.ai.psm.realtime.location.model.vo.GateAccessRecordVO;

import java.time.LocalDateTime;

public interface GateAccessService {

    GateAccessRecordVO ingest(GateRecordIngestRequest request);

    PageResult<GateAccessRecordVO> page(Long tenantId, String gateCode, Long personId,
                                        LocalDateTime fromTime, LocalDateTime toTime, int pageNo, int pageSize);
}
