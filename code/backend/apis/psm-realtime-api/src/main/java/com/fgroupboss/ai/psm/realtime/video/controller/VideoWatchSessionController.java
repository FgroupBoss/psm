package com.fgroupboss.ai.psm.realtime.video.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.realtime.video.model.dto.WatchCaptureRequest;
import com.fgroupboss.ai.psm.realtime.video.model.dto.WatchSessionCloseRequest;
import com.fgroupboss.ai.psm.realtime.video.model.dto.WatchSessionCreateRequest;
import com.fgroupboss.ai.psm.realtime.video.model.vo.VideoWatchCaptureVO;
import com.fgroupboss.ai.psm.realtime.video.model.vo.VideoWatchSessionVO;
import com.fgroupboss.ai.psm.realtime.video.service.VideoWatchSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 作业过程视频监护接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video/watch-sessions")
public class VideoWatchSessionController {

    private final VideoWatchSessionService watchSessionService;

    @PostMapping
    public ResponseVO<VideoWatchSessionVO> create(@Valid @RequestBody WatchSessionCreateRequest request) {
        return ResponseVO.success(watchSessionService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseVO<VideoWatchSessionVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(watchSessionService.getById(tenantId, id));
    }

    @PostMapping("/{id}/capture")
    public ResponseVO<VideoWatchCaptureVO> capture(@PathVariable Long id,
                                                   @Valid @RequestBody WatchCaptureRequest request) {
        return ResponseVO.success(watchSessionService.capture(id, request));
    }

    @PostMapping("/{id}/close")
    public ResponseVO<VideoWatchSessionVO> close(@PathVariable Long id,
                                                 @Valid @RequestBody WatchSessionCloseRequest request) {
        return ResponseVO.success(watchSessionService.close(id, request));
    }
}
