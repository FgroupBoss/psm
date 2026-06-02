package com.fgroupboss.ai.psm.operation.mobile.controller;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.mobile.client.AlarmClient;
import com.fgroupboss.ai.psm.operation.mobile.client.FileClient;
import com.fgroupboss.ai.psm.operation.mobile.client.WorkPermitClient;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.AcceptanceRequest;
import com.fgroupboss.ai.psm.operation.client.dto.AlarmActionRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.CheckInRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.GasTestRequest;
import com.fgroupboss.ai.psm.operation.mobile.client.dto.MeasureConfirmRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.MobileDraftSyncRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.MonitorRecordRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.PermitActionRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.SitePermitRequest;
import com.fgroupboss.ai.psm.operation.client.dto.AlarmEventVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.GasTestVO;
import com.fgroupboss.ai.psm.operation.mobile.client.vo.MobileDraftSyncResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.MonitorRecordVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.SafetyMeasureVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.SiteConfirmVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitVO;
import com.fgroupboss.ai.psm.operation.mobile.config.MobileRole;
import com.fgroupboss.ai.psm.operation.mobile.model.vo.FileUploadVO;
import com.fgroupboss.ai.psm.operation.mobile.model.vo.MobileTaskVO;
import com.fgroupboss.ai.psm.operation.mobile.service.MobileDraftService;
import com.fgroupboss.ai.psm.operation.mobile.service.MobileTaskService;
import com.fgroupboss.ai.psm.operation.mobile.service.MobileWorkPermitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * Mobile 模块 HTTP API。
 * <p>基础路径：{@code /api/mobile}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mobile")
public class MobileController {

    private final MobileTaskService mobileTaskService;
    private final MobileWorkPermitService mobileWorkPermitService;
    private final MobileDraftService mobileDraftService;
    private final AlarmClient alarmClient;
    private final FileClient fileClient;

    /**
     * 查询tasks。
     * <p>HTTP GET {@code /api/mobile/tasks}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param role role 参数
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/tasks")
    public ResponseVO<List<MobileTaskVO>> tasks(@RequestParam Long tenantId,
                                                @RequestParam(required = false) String role,
                                                @RequestParam(required = false) Long userId,
                                                @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userIdHeader) {
        Long resolvedUserId = userId != null ? userId : parseUserId(userIdHeader);
        return ResponseVO.success(mobileTaskService.listTasks(tenantId, resolvedUserId, MobileRole.from(role)));
    }

    /**
     * 查询work permits。
     * <p>HTTP GET {@code /api/mobile/work-permits/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/work-permits/{id}")
    public ResponseVO<WorkPermitDetailVO> workPermitDetail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(mobileWorkPermitService.getDetail(tenantId, id));
    }

    /**
     * 新增check in或触发check in相关动作。
     * <p>HTTP POST {@code /api/mobile/work-permits/{id}/check-in}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/work-permits/{id}/check-in")
    public ResponseVO<SiteConfirmVO> checkIn(@PathVariable Long id,
                                             @Valid @RequestBody CheckInRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(mobileWorkPermitService.checkIn(id, request, contextHeaders(userId, username, tenantIdHeader)));
    }

    /**
     * 新增气体检测记录或触发气体检测记录相关动作。
     * <p>HTTP POST {@code /api/mobile/work-permits/{id}/gas-tests}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/work-permits/{id}/gas-tests")
    public ResponseVO<GasTestVO> gasTest(@PathVariable Long id,
                                         @Valid @RequestBody GasTestRequest request,
                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                         @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(mobileWorkPermitService.addGasTest(id, request, contextHeaders(userId, username, tenantIdHeader)));
    }

    /**
     * 新增confirm或触发confirm相关动作。
     * <p>HTTP POST {@code /api/mobile/work-permits/{id}/measures/confirm}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/work-permits/{id}/measures/confirm")
    public ResponseVO<SafetyMeasureVO> confirmMeasure(@PathVariable Long id,
                                                      @Valid @RequestBody MeasureConfirmRequest request,
                                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                      @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                      @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(mobileWorkPermitService.confirmMeasure(id, request, contextHeaders(userId, username, tenantIdHeader)));
    }

    /**
     * 新增site permit或触发site permit相关动作。
     * <p>HTTP POST {@code /api/mobile/work-permits/{id}/site-permit}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/work-permits/{id}/site-permit")
    public ResponseVO<WorkPermitVO> sitePermit(@PathVariable Long id,
                                               @Valid @RequestBody SitePermitRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(mobileWorkPermitService.sitePermit(id, request, contextHeaders(userId, username, tenantIdHeader)));
    }

    /**
     * 新增monitor records或触发monitor records相关动作。
     * <p>HTTP POST {@code /api/mobile/work-permits/{id}/monitor-records}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/work-permits/{id}/monitor-records")
    public ResponseVO<MonitorRecordVO> monitorRecord(@PathVariable Long id,
                                                     @Valid @RequestBody MonitorRecordRequest request,
                                                     @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                     @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                     @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(mobileWorkPermitService.addMonitorRecord(id, request, contextHeaders(userId, username, tenantIdHeader)));
    }

    /**
     * 暂停/挂起。
     * <p>HTTP POST {@code /api/mobile/work-permits/{id}/suspend}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/work-permits/{id}/suspend")
    public ResponseVO<WorkPermitVO> suspend(@PathVariable Long id,
                                            @RequestParam Long tenantId,
                                            @RequestBody(required = false) PermitActionRequest request,
                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                            @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(mobileWorkPermitService.suspend(id, tenantId, request,
                contextHeaders(userId, username, tenantIdHeader)));
    }

    /**
     * 新增resume或触发resume相关动作。
     * <p>HTTP POST {@code /api/mobile/work-permits/{id}/resume}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/work-permits/{id}/resume")
    public ResponseVO<WorkPermitVO> resume(@PathVariable Long id,
                                           @RequestParam Long tenantId,
                                           @RequestBody(required = false) PermitActionRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(mobileWorkPermitService.resume(id, tenantId, request,
                contextHeaders(userId, username, tenantIdHeader)));
    }

    /**
     * 新增acceptance或触发acceptance相关动作。
     * <p>HTTP POST {@code /api/mobile/work-permits/{id}/acceptance}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/work-permits/{id}/acceptance")
    public ResponseVO<WorkPermitVO> acceptance(@PathVariable Long id,
                                               @Valid @RequestBody AcceptanceRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(mobileWorkPermitService.acceptance(id, request, contextHeaders(userId, username, tenantIdHeader)));
    }

    /**
     * 新增feedback或触发feedback相关动作。
     * <p>HTTP POST {@code /api/mobile/alarms/{id}/feedback}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/alarms/{id}/feedback")
    public ResponseVO<AlarmEventVO> alarmFeedback(@PathVariable Long id,
                                                  @RequestParam Long tenantId,
                                                  @RequestBody(required = false) AlarmActionRequest request,
                                                  @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                  @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                  @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        HttpHeaders headers = contextHeaders(userId, username, tenantIdHeader);
        headers.set("X-Operator", UserContextResolver.operator(userId, username, "mobile"));
        return ResponseVO.success(alarmClient.feedback(tenantId, id, defaultAlarmAction(request), headers));
    }

    /**
     * 新增upload或触发upload相关动作。
     * <p>HTTP POST {@code /api/mobile/files/upload}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param file file 参数
     * @param bizType 业务类型
     * @param bizId 业务实体 ID
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/files/upload")
    public ResponseVO<FileUploadVO> uploadFile(@RequestParam Long tenantId,
                                               @RequestParam("file") MultipartFile file,
                                               @RequestParam(required = false) String bizType,
                                               @RequestParam(required = false) Long bizId,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(fileClient.upload(tenantId, file, bizType, bizId,
                contextHeaders(userId, username, tenantIdHeader)));
    }

    /**
     * 新增sync或触发sync相关动作。
     * <p>HTTP POST {@code /api/mobile/drafts/sync}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/drafts/sync")
    public ResponseVO<MobileDraftSyncResultVO> syncDraft(@Valid @RequestBody MobileDraftSyncRequest request,
                                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                         @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        if (request.getUserId() == null && StringUtils.hasText(userId)) {
            request.setUserId(parseUserId(userId));
        }
        return ResponseVO.success(mobileDraftService.sync(request, contextHeaders(userId, username, tenantIdHeader)));
    }

    private AlarmActionRequest defaultAlarmAction(AlarmActionRequest request) {
        return request == null ? new AlarmActionRequest() : request;
    }

    private HttpHeaders contextHeaders(String userId, String username, String tenantIdHeader) {
        return WorkPermitClient.buildContextHeaders(userId, username, tenantIdHeader);
    }

    private Long parseUserId(String userIdHeader) {
        if (!StringUtils.hasText(userIdHeader)) {
            return null;
        }
        try {
            return Long.valueOf(userIdHeader.trim());
        } catch (NumberFormatException ex) {
            throw new BusinessException(400, "invalid user id header");
        }
    }
}
