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
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
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
 * Alarm 模块 HTTP API。
 * <p>实时告警事件与规则配置。</p>
 * <p>基础路径：{@code /api/alarms}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmController {

    private final AlarmService alarmService;
    /**
     * 服务健康检查。
     * <p>HTTP GET {@code /api/alarms/health}</p>
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/health")
    public ResponseVO<AlarmHealthVO> health() {
        return ResponseVO.success(alarmService.health());
    }
    /**
     * 新增ingest或触发ingest相关动作。
     * <p>HTTP POST {@code /api/alarms/ingest}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/ingest")
    public ResponseVO<AlarmEventVO> ingest(@Valid @RequestBody AlarmIngestRequest request) {
        return ResponseVO.success(alarmService.ingest(request));
    }
    /**
     * 新增area active check或触发area active check相关动作。
     * <p>HTTP POST {@code /api/alarms/area-active-check}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/area-active-check")
    public ResponseVO<AlarmAreaActiveCheckVO> areaActiveCheck(@Valid @RequestBody AlarmAreaActiveCheckRequest request) {
        return ResponseVO.success(alarmService.areaActiveCheck(request));
    }
    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/alarms}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param alarmLevel alarmLevel 参数
     * @param areaId 区域 ID
     * @param hazardId 重大危险源 ID
     * @param sourceType sourceType 参数
     * @param occurredFrom occurredFrom 参数
     * @param occurredTo occurredTo 参数
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<AlarmEventVO>> page(@LoginContext UserContext loginContext,
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
        return ResponseVO.success(alarmService.page(loginContext.getTenantId(), keyword, status, alarmLevel, areaId, hazardId, sourceType,
                occurredFrom, occurredTo, pageNo, pageSize));
    }
    /**
     * 查询作业票详情。
     * <p>HTTP GET {@code /api/alarms/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<AlarmDetailVO> detail(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(alarmService.getDetail(loginContext.getTenantId(), id));
    }
    /**
     * 新增confirm或触发confirm相关动作。
     * <p>HTTP POST {@code /api/alarms/{id}/confirm}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/confirm")
    public ResponseVO<AlarmEventVO> confirm(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                      @RequestBody(required = false) AlarmActionRequest request,
                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(alarmService.confirm(loginContext.getTenantId(), id, defaultRequest(request), UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 新增dispatch或触发dispatch相关动作。
     * <p>HTTP POST {@code /api/alarms/{id}/dispatch}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/dispatch")
    public ResponseVO<AlarmEventVO> dispatch(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                          @RequestBody(required = false) AlarmActionRequest request,
                                                                                                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(alarmService.dispatch(loginContext.getTenantId(), id, defaultRequest(request), UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 新增feedback或触发feedback相关动作。
     * <p>HTTP POST {@code /api/alarms/{id}/feedback}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/feedback")
    public ResponseVO<AlarmEventVO> feedback(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                          @RequestBody(required = false) AlarmActionRequest request,
                                                                                                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(alarmService.feedback(loginContext.getTenantId(), id, defaultRequest(request), UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 新增close或触发close相关动作。
     * <p>HTTP POST {@code /api/alarms/{id}/close}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/close")
    public ResponseVO<AlarmEventVO> close(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                  @RequestBody(required = false) AlarmActionRequest request,
                                                                                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(alarmService.close(loginContext.getTenantId(), id, defaultRequest(request), UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 新增false close或触发false close相关动作。
     * <p>HTTP POST {@code /api/alarms/{id}/false-close}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/false-close")
    public ResponseVO<AlarmEventVO> falseClose(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                              @Valid @RequestBody AlarmFalseCloseRequest request,
                                                                                                                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(alarmService.falseClose(loginContext.getTenantId(), id, request, UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 新增to hazard或触发to hazard相关动作。
     * <p>HTTP POST {@code /api/alarms/{id}/to-hazard}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/to-hazard")
    public ResponseVO<RemoteHazardReportVO> toHazard(@PathVariable Long id,
                                                     @Valid @RequestBody AlarmToHazardRequest request) {
        return ResponseVO.success(alarmService.toHazard(request.getTenantId(), id, request));
    }

    private AlarmActionRequest defaultRequest(AlarmActionRequest request) {
        return request == null ? new AlarmActionRequest() : request;
    }

}

