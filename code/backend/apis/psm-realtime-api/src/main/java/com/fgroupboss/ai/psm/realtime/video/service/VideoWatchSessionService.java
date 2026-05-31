package com.fgroupboss.ai.psm.realtime.video.service;

import com.fgroupboss.ai.psm.realtime.video.model.dto.WatchCaptureRequest;
import com.fgroupboss.ai.psm.realtime.video.model.dto.WatchSessionCloseRequest;
import com.fgroupboss.ai.psm.realtime.video.model.dto.WatchSessionCreateRequest;
import com.fgroupboss.ai.psm.realtime.video.model.vo.VideoWatchCaptureVO;
import com.fgroupboss.ai.psm.realtime.video.model.vo.VideoWatchSessionVO;

public interface VideoWatchSessionService {

    VideoWatchSessionVO create(WatchSessionCreateRequest request);

    VideoWatchSessionVO getById(Long tenantId, Long id);

    VideoWatchCaptureVO capture(Long sessionId, WatchCaptureRequest request);

    VideoWatchSessionVO close(Long sessionId, WatchSessionCloseRequest request);
}
