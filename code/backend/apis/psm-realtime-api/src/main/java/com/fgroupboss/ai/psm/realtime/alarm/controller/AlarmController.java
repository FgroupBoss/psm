package com.fgroupboss.ai.psm.realtime.alarm.controller;

import com.fgroupboss.ai.psm.realtime.api.alarm.dto.AlarmActionRequest;
import com.fgroupboss.ai.psm.realtime.api.alarm.dto.AlarmAreaActiveCheckRequest;
import com.fgroupboss.ai.psm.realtime.alarm.model.dto.AlarmFalseCloseRequest;
import com.fgroupboss.ai.psm.realtime.alarm.model.dto.AlarmIngestRequest;
import com.fgroupboss.ai.psm.realtime.alarm.model.dto.AlarmToHazardRequest;
import com.fgroupboss.ai.psm.realtime.alarm.client.RemoteHazardReportVO;
import com.fgroupboss.ai.psm.realtime.api.alarm.vo.AlarmAreaActiveCheckVO;
import com.fgroupboss.ai.psm.realtime.alarm.model.vo.AlarmDetailVO;
import com.fgroupboss.ai.psm.realtime.api.alarm.vo.AlarmEventVO;
import com.fgroupboss.ai.psm.realtime.alarm.model.vo.AlarmHealthVO;
import com.fgroupboss.ai.psm.realtime.alarm.service.AlarmService;
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
 * 鎶ヨ涓績鎺ュ彛锛堟壒娆?6锛氬畬鏁寸敓鍛藉懆鏈?+ 鍖哄煙娲昏穬妫€鏌ワ級銆?
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmController {

    private final AlarmService alarmService;

    /**
     * 鎺ュ彛鐢ㄩ€旓細鏌ヨ鏈嶅姟鍋ュ悍鐘舵€併€?
     */
    @GetMapping("/health")
    public ResponseVO<AlarmHealthVO> health() {
        return ResponseVO.success(alarmService.health());
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鎺ュ叆鎶ヨ浜嬩欢銆?
     */
    @PostMapping("/ingest")
    public ResponseVO<AlarmEventVO> ingest(@Valid @RequestBody AlarmIngestRequest request) {
        return ResponseVO.success(alarmService.ingest(request));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細妫€鏌ュ尯鍩熸椿璺冩姤璀︺€?
     */
    @PostMapping("/area-active-check")
    public ResponseVO<AlarmAreaActiveCheckVO> areaActiveCheck(@Valid @RequestBody AlarmAreaActiveCheckRequest request) {
        return ResponseVO.success(alarmService.areaActiveCheck(request));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鍒嗛〉鏌ヨ涓氬姟鏁版嵁銆?
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
     * 鎺ュ彛鐢ㄩ€旓細鏌ヨ璇︽儏銆?
     */
    @GetMapping("/{id}")
    public ResponseVO<AlarmDetailVO> detail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(alarmService.getDetail(tenantId, id));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細纭鎶ヨ銆?
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
     * 鎺ュ彛鐢ㄩ€旓細娲惧彂鎶ヨ銆?
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
     * 鎺ュ彛鐢ㄩ€旓細鍙嶉鎶ヨ澶勭悊缁撴灉銆?
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
     * 鎺ュ彛鐢ㄩ€旓細鍏抽棴鎶ヨ銆?
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
     * 鎺ュ彛鐢ㄩ€旓細璇姤鍏抽棴鎶ヨ銆?
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

    /**
     * 鎺ュ彛鐢ㄩ€旓細鎶ヨ涓€閿浆闅愭偅銆?
     */
    @PostMapping("/{id}/to-hazard")
    public ResponseVO<RemoteHazardReportVO> toHazard(@PathVariable Long id,
                                                     @Valid @RequestBody AlarmToHazardRequest request) {
        return ResponseVO.success(alarmService.toHazard(request.getTenantId(), id, request));
    }

    private AlarmActionRequest defaultRequest(AlarmActionRequest request) {
        return request == null ? new AlarmActionRequest() : request;
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}

