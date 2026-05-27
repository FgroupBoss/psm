package com.fgroupboss.ai.psm.alarm.controller;

import com.fgroupboss.ai.psm.alarm.model.dto.AlarmActionRequest;
import com.fgroupboss.ai.psm.alarm.model.dto.AlarmAreaActiveCheckRequest;
import com.fgroupboss.ai.psm.alarm.model.dto.AlarmFalseCloseRequest;
import com.fgroupboss.ai.psm.alarm.model.dto.AlarmIngestRequest;
import com.fgroupboss.ai.psm.alarm.model.vo.AlarmAreaActiveCheckVO;
import com.fgroupboss.ai.psm.alarm.model.vo.AlarmDetailVO;
import com.fgroupboss.ai.psm.alarm.model.vo.AlarmEventVO;
import com.fgroupboss.ai.psm.alarm.model.vo.AlarmHealthVO;
import com.fgroupboss.ai.psm.alarm.service.AlarmService;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
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
import java.util.Date;

/**
 * 报警中心接口（批次 6：完整生命周期 + 区域活跃检查）。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmController {

    private final AlarmService alarmService;

    /**
     * 接口用途：查询服务健康状态。
     */
    @GetMapping("/health")
    public ResponseVO<AlarmHealthVO> health() {
        return ResponseVO.success(alarmService.health());
    }

    /**
     * 接口用途：接入报警事件。
     */
    @PostMapping("/ingest")
    public ResponseVO<AlarmEventVO> ingest(@Valid @RequestBody AlarmIngestRequest request) {
        return ResponseVO.success(alarmService.ingest(request));
    }

    /**
     * 接口用途：检查区域活跃报警。
     */
    @PostMapping("/area-active-check")
    public ResponseVO<AlarmAreaActiveCheckVO> areaActiveCheck(@Valid @RequestBody AlarmAreaActiveCheckRequest request) {
        return ResponseVO.success(alarmService.areaActiveCheck(request));
    }

    /**
     * 接口用途：分页查询业务数据。
     */
    @GetMapping
    public ResponseVO<PageResult<AlarmEventVO>> page(@RequestParam Long tenantId,
                                                     @RequestParam(required = false) String keyword,
                                                     @RequestParam(required = false) String status,
                                                     @RequestParam(required = false) String alarmLevel,
                                                     @RequestParam(required = false) Long areaId,
                                                     @RequestParam(required = false) Long hazardId,
                                                     @RequestParam(required = false) String sourceType,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date occurredFrom,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date occurredTo,
                                                     @RequestParam(defaultValue = "1") int pageNo,
                                                     @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(alarmService.page(tenantId, keyword, status, alarmLevel, areaId, hazardId, sourceType,
                occurredFrom, occurredTo, pageNo, pageSize));
    }

    /**
     * 接口用途：查询详情。
     */
    @GetMapping("/{id}")
    public ResponseVO<AlarmDetailVO> detail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(alarmService.getDetail(tenantId, id));
    }

    /**
     * 接口用途：确认报警。
     */
    @PostMapping("/{id}/confirm")
    public ResponseVO<AlarmEventVO> confirm(@PathVariable Long id,
                                           @RequestParam Long tenantId,
                                           @RequestBody(required = false) AlarmActionRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(alarmService.confirm(tenantId, id, defaultRequest(request), operator(userId, username, operator)));
    }

    /**
     * 接口用途：派发报警。
     */
    @PostMapping("/{id}/dispatch")
    public ResponseVO<AlarmEventVO> dispatch(@PathVariable Long id,
                                             @RequestParam Long tenantId,
                                             @RequestBody(required = false) AlarmActionRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(alarmService.dispatch(tenantId, id, defaultRequest(request), operator(userId, username, operator)));
    }

    /**
     * 接口用途：反馈报警处理结果。
     */
    @PostMapping("/{id}/feedback")
    public ResponseVO<AlarmEventVO> feedback(@PathVariable Long id,
                                             @RequestParam Long tenantId,
                                             @RequestBody(required = false) AlarmActionRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(alarmService.feedback(tenantId, id, defaultRequest(request), operator(userId, username, operator)));
    }

    /**
     * 接口用途：关闭报警。
     */
    @PostMapping("/{id}/close")
    public ResponseVO<AlarmEventVO> close(@PathVariable Long id,
                                         @RequestParam Long tenantId,
                                         @RequestBody(required = false) AlarmActionRequest request,
                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(alarmService.close(tenantId, id, defaultRequest(request), operator(userId, username, operator)));
    }

    /**
     * 接口用途：误报关闭报警。
     */
    @PostMapping("/{id}/false-close")
    public ResponseVO<AlarmEventVO> falseClose(@PathVariable Long id,
                                               @RequestParam Long tenantId,
                                               @Valid @RequestBody AlarmFalseCloseRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(alarmService.falseClose(tenantId, id, request, operator(userId, username, operator)));
    }

    private AlarmActionRequest defaultRequest(AlarmActionRequest request) {
        return request == null ? new AlarmActionRequest() : request;
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
