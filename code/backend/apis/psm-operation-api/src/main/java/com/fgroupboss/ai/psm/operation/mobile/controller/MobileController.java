package com.fgroupboss.ai.psm.operation.mobile.controller;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContext;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * Mobile 模块 HTTP API。
 * <p>基础路径：{@code /api/mobile}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；租户与用户 ID 从登录上下文解析。</p>
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
     * 查询 tasks。
     * <p>HTTP GET {@code /api/mobile/tasks}</p>
     *
     * @param loginContext 当前登录租户与用户
     * @param role         角色视角（可选）
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/tasks")
    public ResponseVO<List<MobileTaskVO>> tasks(@LoginContext UserContext loginContext,
                                                @RequestParam(required = false) String role) {
        return ResponseVO.success(mobileTaskService.listTasks(
                loginContext.getTenantId(), loginContext.getUserId(), MobileRole.from(role)));
    }

    /**
     * 查询作业票详情。
     * <p>HTTP GET {@code /api/mobile/work-permits/{id}}</p>
     *
     * @param loginContext 当前登录租户
     * @param id           作业票 ID
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/work-permits/{id}")
    public ResponseVO<WorkPermitDetailVO> workPermitDetail(@LoginContext UserContext loginContext,
                                                           @PathVariable Long id) {
        return ResponseVO.success(mobileWorkPermitService.getDetail(loginContext.getTenantId(), id));
    }

    @PostMapping("/work-permits/{id}/check-in")
    public ResponseVO<SiteConfirmVO> checkIn(@LoginContext UserContext loginContext,
                                             @PathVariable Long id,
                                             @Valid @RequestBody CheckInRequest request) {
        return ResponseVO.success(mobileWorkPermitService.checkIn(id, request, contextHeaders(loginContext)));
    }

    @PostMapping("/work-permits/{id}/gas-tests")
    public ResponseVO<GasTestVO> gasTest(@LoginContext UserContext loginContext,
                                         @PathVariable Long id,
                                         @Valid @RequestBody GasTestRequest request) {
        return ResponseVO.success(mobileWorkPermitService.addGasTest(id, request, contextHeaders(loginContext)));
    }

    @PostMapping("/work-permits/{id}/measures/confirm")
    public ResponseVO<SafetyMeasureVO> confirmMeasure(@LoginContext UserContext loginContext,
                                                        @PathVariable Long id,
                                                        @Valid @RequestBody MeasureConfirmRequest request) {
        return ResponseVO.success(mobileWorkPermitService.confirmMeasure(id, request, contextHeaders(loginContext)));
    }

    @PostMapping("/work-permits/{id}/site-permit")
    public ResponseVO<WorkPermitVO> sitePermit(@LoginContext UserContext loginContext,
                                               @PathVariable Long id,
                                               @Valid @RequestBody SitePermitRequest request) {
        return ResponseVO.success(mobileWorkPermitService.sitePermit(id, request, contextHeaders(loginContext)));
    }

    @PostMapping("/work-permits/{id}/monitor-records")
    public ResponseVO<MonitorRecordVO> monitorRecord(@LoginContext UserContext loginContext,
                                                     @PathVariable Long id,
                                                     @Valid @RequestBody MonitorRecordRequest request) {
        return ResponseVO.success(mobileWorkPermitService.addMonitorRecord(id, request, contextHeaders(loginContext)));
    }

    @PostMapping("/work-permits/{id}/suspend")
    public ResponseVO<WorkPermitVO> suspend(@LoginContext UserContext loginContext,
                                            @PathVariable Long id,
                                            @RequestBody(required = false) PermitActionRequest request) {
        return ResponseVO.success(mobileWorkPermitService.suspend(
                id, loginContext.getTenantId(), request, contextHeaders(loginContext)));
    }

    @PostMapping("/work-permits/{id}/resume")
    public ResponseVO<WorkPermitVO> resume(@LoginContext UserContext loginContext,
                                           @PathVariable Long id,
                                           @RequestBody(required = false) PermitActionRequest request) {
        return ResponseVO.success(mobileWorkPermitService.resume(
                id, loginContext.getTenantId(), request, contextHeaders(loginContext)));
    }

    @PostMapping("/work-permits/{id}/acceptance")
    public ResponseVO<WorkPermitVO> acceptance(@LoginContext UserContext loginContext,
                                               @PathVariable Long id,
                                               @Valid @RequestBody AcceptanceRequest request) {
        return ResponseVO.success(mobileWorkPermitService.acceptance(id, request, contextHeaders(loginContext)));
    }

    @PostMapping("/alarms/{id}/feedback")
    public ResponseVO<AlarmEventVO> alarmFeedback(@LoginContext UserContext loginContext,
                                                  @PathVariable Long id,
                                                  @RequestBody(required = false) AlarmActionRequest request) {
        HttpHeaders headers = contextHeaders(loginContext);
        headers.set("X-Operator", UserContextResolver.operator(loginContext, "mobile"));
        return ResponseVO.success(alarmClient.feedback(
                loginContext.getTenantId(), id, defaultAlarmAction(request), headers));
    }

    @PostMapping("/files/upload")
    public ResponseVO<FileUploadVO> uploadFile(@LoginContext UserContext loginContext,
                                               @RequestParam("file") MultipartFile file,
                                               @RequestParam(required = false) String bizType,
                                               @RequestParam(required = false) Long bizId) {
        return ResponseVO.success(fileClient.upload(
                loginContext.getTenantId(), file, bizType, bizId, contextHeaders(loginContext)));
    }

    @PostMapping("/drafts/sync")
    public ResponseVO<MobileDraftSyncResultVO> syncDraft(@LoginContext UserContext loginContext,
                                                         @Valid @RequestBody MobileDraftSyncRequest request) {
        if (request.getUserId() == null && loginContext.getUserId() != null) {
            request.setUserId(loginContext.getUserId());
        }
        return ResponseVO.success(mobileDraftService.sync(request, contextHeaders(loginContext)));
    }

    private AlarmActionRequest defaultAlarmAction(AlarmActionRequest request) {
        return request == null ? new AlarmActionRequest() : request;
    }

    private HttpHeaders contextHeaders(UserContext loginContext) {
        return WorkPermitClient.buildContextHeaders(
                loginContext.getUserId() == null ? null : String.valueOf(loginContext.getUserId()),
                loginContext.getUsername(),
                loginContext.getTenantId() == null ? null : String.valueOf(loginContext.getTenantId()));
    }
}
