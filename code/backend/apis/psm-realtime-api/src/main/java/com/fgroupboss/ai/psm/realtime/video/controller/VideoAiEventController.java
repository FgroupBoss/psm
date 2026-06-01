package com.fgroupboss.ai.psm.realtime.video.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.realtime.video.model.dto.AiEventIgnoreRequest;
import com.fgroupboss.ai.psm.realtime.video.model.dto.AiEventIngestRequest;
import com.fgroupboss.ai.psm.realtime.video.model.vo.VideoAiEventVO;
import com.fgroupboss.ai.psm.realtime.video.service.VideoAiEventService;
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
 * VideoAiEvent 模块 HTTP API。
 * <p>基础路径：{@code /api/video/ai-events}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video/ai-events")
public class VideoAiEventController {

    private final VideoAiEventService aiEventService;

    /**
     * 新增ingest或触发ingest相关动作。
     * <p>HTTP POST {@code /api/video/ai-events/ingest}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/ingest")
    public ResponseVO<VideoAiEventVO> ingest(@Valid @RequestBody AiEventIngestRequest request) {
        return ResponseVO.success(aiEventService.ingest(request));
    }

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/video/ai-events}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param cameraId camera ID
     * @param eventType eventType 参数
     * @param status 业务状态筛选
     * @param severity severity 参数
     * @param areaId 区域 ID
     * @param occurredFrom occurredFrom 参数
     * @param occurredTo occurredTo 参数
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/video/ai-events/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<VideoAiEventVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(aiEventService.getById(tenantId, id));
    }

    /**
     * 新增to alarm或触发to alarm相关动作。
     * <p>HTTP POST {@code /api/video/ai-events/{id}/to-alarm}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/to-alarm")
    public ResponseVO<VideoAiEventVO> toAlarm(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(aiEventService.toAlarm(tenantId, id));
    }

    /**
     * 新增ignore或触发ignore相关动作。
     * <p>HTTP POST {@code /api/video/ai-events/{id}/ignore}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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
