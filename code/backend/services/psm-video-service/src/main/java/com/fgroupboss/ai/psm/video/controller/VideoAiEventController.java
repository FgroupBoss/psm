package com.fgroupboss.ai.psm.video.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.video.model.dto.AiEventIgnoreRequest;
import com.fgroupboss.ai.psm.video.model.dto.AiEventIngestRequest;
import com.fgroupboss.ai.psm.video.model.vo.VideoAiEventVO;
import com.fgroupboss.ai.psm.video.service.VideoAiEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * AI 视频事件接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video/ai-events")
public class VideoAiEventController {

    private final VideoAiEventService aiEventService;

    @PostMapping("/ingest")
    public ResponseVO<VideoAiEventVO> ingest(@Valid @RequestBody AiEventIngestRequest request) {
        return ResponseVO.success(aiEventService.ingest(request));
    }

    @GetMapping
    public ResponseVO<PageResult<VideoAiEventVO>> page(@RequestParam Long tenantId,
                                                       @RequestParam(required = false) Long cameraId,
                                                       @RequestParam(required = false) String eventType,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(required = false) String severity,
                                                       @RequestParam(required = false) Long areaId,
                                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime occurredFrom,
                                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime occurredTo,
                                                       @RequestParam(defaultValue = "1") int pageNo,
                                                       @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(aiEventService.page(tenantId, cameraId, eventType, status, severity, areaId,
                occurredFrom, occurredTo, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseVO<VideoAiEventVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(aiEventService.getById(tenantId, id));
    }

    @PostMapping("/{id}/to-alarm")
    public ResponseVO<VideoAiEventVO> toAlarm(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(aiEventService.toAlarm(tenantId, id));
    }

    @PostMapping("/{id}/ignore")
    public ResponseVO<VideoAiEventVO> ignore(@PathVariable Long id,
                                             @RequestParam Long tenantId,
                                             @Valid @RequestBody AiEventIgnoreRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(aiEventService.ignore(tenantId, id, request, operator(userId, username, operator)));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
