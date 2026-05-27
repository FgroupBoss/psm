package com.fgroupboss.ai.psm.mobile.controller;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.mobile.client.AlarmClient;
import com.fgroupboss.ai.psm.mobile.client.WorkPermitClient;
import com.fgroupboss.ai.psm.mobile.client.dto.AcceptanceRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.AlarmActionRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.CheckInRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.GasTestRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.MeasureConfirmRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.MobileDraftSyncRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.MonitorRecordRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.PermitActionRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.SitePermitRequest;
import com.fgroupboss.ai.psm.mobile.client.vo.AlarmEventVO;
import com.fgroupboss.ai.psm.mobile.client.vo.GasTestVO;
import com.fgroupboss.ai.psm.mobile.client.vo.MobileDraftSyncResultVO;
import com.fgroupboss.ai.psm.mobile.client.vo.MonitorRecordVO;
import com.fgroupboss.ai.psm.mobile.client.vo.SafetyMeasureVO;
import com.fgroupboss.ai.psm.mobile.client.vo.SiteConfirmVO;
import com.fgroupboss.ai.psm.mobile.client.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.mobile.client.vo.WorkPermitVO;
import com.fgroupboss.ai.psm.mobile.config.MobileRole;
import com.fgroupboss.ai.psm.mobile.model.vo.FileUploadVO;
import com.fgroupboss.ai.psm.mobile.model.vo.MobileTaskVO;
import com.fgroupboss.ai.psm.mobile.service.MobileDraftService;
import com.fgroupboss.ai.psm.mobile.service.MobileTaskService;
import com.fgroupboss.ai.psm.mobile.service.MobileWorkPermitService;
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

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 移动端现场作业 BFF 接口（M07）。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mobile")
public class MobileController {

    private final MobileTaskService mobileTaskService;
    private final MobileWorkPermitService mobileWorkPermitService;
    private final MobileDraftService mobileDraftService;
    private final AlarmClient alarmClient;

    @GetMapping("/tasks")
    public ResponseVO<List<MobileTaskVO>> tasks(@RequestParam Long tenantId,
                                                @RequestParam(required = false) String role,
                                                @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userIdHeader) {
        Long userId = parseUserId(userIdHeader);
        return ResponseVO.success(mobileTaskService.listTasks(tenantId, userId, MobileRole.from(role)));
    }

    @GetMapping("/work-permits/{id}")
    public ResponseVO<WorkPermitDetailVO> workPermitDetail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(mobileWorkPermitService.getDetail(tenantId, id));
    }

    @PostMapping("/work-permits/{id}/check-in")
    public ResponseVO<SiteConfirmVO> checkIn(@PathVariable Long id,
                                             @Valid @RequestBody CheckInRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(mobileWorkPermitService.checkIn(id, request, contextHeaders(userId, username, tenantIdHeader)));
    }

    @PostMapping("/work-permits/{id}/gas-tests")
    public ResponseVO<GasTestVO> gasTest(@PathVariable Long id,
                                         @Valid @RequestBody GasTestRequest request,
                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                         @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(mobileWorkPermitService.addGasTest(id, request, contextHeaders(userId, username, tenantIdHeader)));
    }

    @PostMapping("/work-permits/{id}/measures/confirm")
    public ResponseVO<SafetyMeasureVO> confirmMeasure(@PathVariable Long id,
                                                      @Valid @RequestBody MeasureConfirmRequest request,
                                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                      @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                      @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(mobileWorkPermitService.confirmMeasure(id, request, contextHeaders(userId, username, tenantIdHeader)));
    }

    @PostMapping("/work-permits/{id}/site-permit")
    public ResponseVO<WorkPermitVO> sitePermit(@PathVariable Long id,
                                               @Valid @RequestBody SitePermitRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(mobileWorkPermitService.sitePermit(id, request, contextHeaders(userId, username, tenantIdHeader)));
    }

    @PostMapping("/work-permits/{id}/monitor-records")
    public ResponseVO<MonitorRecordVO> monitorRecord(@PathVariable Long id,
                                                     @Valid @RequestBody MonitorRecordRequest request,
                                                     @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                     @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                     @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(mobileWorkPermitService.addMonitorRecord(id, request, contextHeaders(userId, username, tenantIdHeader)));
    }

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

    @PostMapping("/work-permits/{id}/acceptance")
    public ResponseVO<WorkPermitVO> acceptance(@PathVariable Long id,
                                               @Valid @RequestBody AcceptanceRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantIdHeader) {
        return ResponseVO.success(mobileWorkPermitService.acceptance(id, request, contextHeaders(userId, username, tenantIdHeader)));
    }

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

    @PostMapping("/files/upload")
    public ResponseVO<FileUploadVO> uploadFile(@RequestParam(required = false) String fileName,
                                               @RequestParam(required = false) String contentType,
                                               @RequestParam(required = false) Long sizeBytes,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId) {
        FileUploadVO vo = new FileUploadVO();
        vo.setFileId(UUID.randomUUID().toString().replace("-", ""));
        vo.setFileName(StringUtils.hasText(fileName) ? fileName : "mobile-upload.bin");
        vo.setContentType(StringUtils.hasText(contentType) ? contentType : "application/octet-stream");
        vo.setSizeBytes(sizeBytes == null ? 0L : sizeBytes);
        vo.setSha256("mock-" + vo.getFileId());
        vo.setUrl("/mock/files/" + vo.getFileId());
        vo.setUploadedAt(new Date());
        return ResponseVO.success(vo);
    }

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
