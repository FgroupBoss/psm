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
 * VideoWatchSession 模块 HTTP API。
 * <p>基础路径：{@code /api/video/watch-sessions}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video/watch-sessions")
public class VideoWatchSessionController {

    private final VideoWatchSessionService watchSessionService;

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/video/watch-sessions}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<VideoWatchSessionVO> create(@Valid @RequestBody WatchSessionCreateRequest request) {
        return ResponseVO.success(watchSessionService.create(request));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/video/watch-sessions/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<VideoWatchSessionVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(watchSessionService.getById(tenantId, id));
    }

    /**
     * 新增capture或触发capture相关动作。
     * <p>HTTP POST {@code /api/video/watch-sessions/{id}/capture}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/capture")
    public ResponseVO<VideoWatchCaptureVO> capture(@PathVariable Long id,
                                                   @Valid @RequestBody WatchCaptureRequest request) {
        return ResponseVO.success(watchSessionService.capture(id, request));
    }

    /**
     * 新增close或触发close相关动作。
     * <p>HTTP POST {@code /api/video/watch-sessions/{id}/close}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/close")
    public ResponseVO<VideoWatchSessionVO> close(@PathVariable Long id,
                                                 @Valid @RequestBody WatchSessionCloseRequest request) {
        return ResponseVO.success(watchSessionService.close(id, request));
    }
}
