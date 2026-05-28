package com.fgroupboss.ai.psm.video.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.video.model.dto.AiEventIgnoreRequest;
import com.fgroupboss.ai.psm.video.model.dto.AiEventIngestRequest;
import com.fgroupboss.ai.psm.video.model.vo.VideoAiEventVO;

import java.time.LocalDateTime;

public interface VideoAiEventService {

    VideoAiEventVO ingest(AiEventIngestRequest request);

    PageResult<VideoAiEventVO> page(Long tenantId, Long cameraId, String eventType, String status,
                                    String severity, Long areaId, LocalDateTime occurredFrom,
                                    LocalDateTime occurredTo, int pageNo, int pageSize);

    VideoAiEventVO getById(Long tenantId, Long id);

    VideoAiEventVO toAlarm(Long tenantId, Long id);

    VideoAiEventVO ignore(Long tenantId, Long id, AiEventIgnoreRequest request, String operator);
}
