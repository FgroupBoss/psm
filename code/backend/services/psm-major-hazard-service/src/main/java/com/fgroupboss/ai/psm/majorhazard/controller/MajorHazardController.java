package com.fgroupboss.ai.psm.majorhazard.controller;

import com.fgroupboss.ai.psm.common.DemoInfo;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.majorhazard.model.dto.HazardAttachmentRequest;
import com.fgroupboss.ai.psm.majorhazard.model.dto.HazardPointRequest;
import com.fgroupboss.ai.psm.majorhazard.model.dto.HazardStatusRequest;
import com.fgroupboss.ai.psm.majorhazard.model.dto.MajorHazardRequest;
import com.fgroupboss.ai.psm.majorhazard.model.dto.ResponsibilityReplaceRequest;
import com.fgroupboss.ai.psm.majorhazard.model.dto.RiskContextRequest;
import com.fgroupboss.ai.psm.majorhazard.model.vo.HazardAlarmSummaryVO;
import com.fgroupboss.ai.psm.majorhazard.model.vo.HazardAttachmentVO;
import com.fgroupboss.ai.psm.majorhazard.model.vo.HazardPointVO;
import com.fgroupboss.ai.psm.majorhazard.model.vo.MajorHazardResponsibilityVO;
import com.fgroupboss.ai.psm.majorhazard.model.vo.MajorHazardVO;
import com.fgroupboss.ai.psm.majorhazard.model.vo.RiskContextVO;
import com.fgroupboss.ai.psm.majorhazard.service.HazardAlarmQueryService;
import com.fgroupboss.ai.psm.majorhazard.service.MajorHazardAttachmentService;
import com.fgroupboss.ai.psm.majorhazard.service.MajorHazardPointService;
import com.fgroupboss.ai.psm.majorhazard.service.MajorHazardService;
import com.fgroupboss.ai.psm.majorhazard.service.RiskContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 重大危险源管理接口（批次 6：报警 Tab 联动）。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/major-hazards")
public class MajorHazardController {

    private final MajorHazardService hazardService;
    private final MajorHazardPointService pointService;
    private final MajorHazardAttachmentService attachmentService;
    private final RiskContextService riskContextService;
    private final HazardAlarmQueryService hazardAlarmQueryService;

    @GetMapping("/health")
    public ResponseVO<DemoInfo> health() {
        return ResponseVO.success(new DemoInfo("psm-major-hazard-service", "major-hazard", "1.0.0-batch5"));
    }

    @PostMapping("/risk-context")
    public ResponseVO<RiskContextVO> riskContext(@Valid @RequestBody RiskContextRequest request) {
        return ResponseVO.success(riskContextService.query(request));
    }

    @GetMapping
    public ResponseVO<PageResult<MajorHazardVO>> page(@RequestParam Long tenantId,
                                                      @RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) String status,
                                                      @RequestParam(required = false) String level,
                                                      @RequestParam(defaultValue = "1") int pageNo,
                                                      @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(hazardService.page(tenantId, keyword, status, level, pageNo, pageSize));
    }

    @PostMapping
    public ResponseVO<MajorHazardVO> create(@Valid @RequestBody MajorHazardRequest request,
                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardService.create(request, operator(userId, username, operator)));
    }

    @GetMapping("/{id}")
    public ResponseVO<MajorHazardVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(hazardService.getById(tenantId, id));
    }

    @PutMapping("/{id}")
    public ResponseVO<MajorHazardVO> update(@PathVariable Long id,
                                            @Valid @RequestBody MajorHazardRequest request,
                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardService.update(id, request, operator(userId, username, operator)));
    }

    @PostMapping("/{id}/publish")
    public ResponseVO<MajorHazardVO> publish(@PathVariable Long id,
                                             @RequestParam Long tenantId,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardService.publish(tenantId, id, operator(userId, username, operator)));
    }

    @PostMapping("/{id}/status")
    public ResponseVO<MajorHazardVO> changeStatus(@PathVariable Long id,
                                                  @RequestParam Long tenantId,
                                                  @Valid @RequestBody HazardStatusRequest request,
                                                  @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                  @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardService.changeStatus(tenantId, id, request, operator(userId, username, operator)));
    }

    @GetMapping("/{id}/responsibilities")
    public ResponseVO<List<MajorHazardResponsibilityVO>> listResponsibilities(@PathVariable Long id,
                                                                                @RequestParam Long tenantId) {
        return ResponseVO.success(hazardService.listResponsibilities(tenantId, id));
    }

    @PutMapping("/{id}/responsibilities")
    public ResponseVO<List<MajorHazardResponsibilityVO>> replaceResponsibilities(
            @PathVariable Long id,
            @RequestParam Long tenantId,
            @Valid @RequestBody ResponsibilityReplaceRequest request,
            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardService.replaceResponsibilities(tenantId, id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/{id}/points")
    public ResponseVO<List<HazardPointVO>> listPoints(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(pointService.listPoints(tenantId, id));
    }

    @PostMapping("/{id}/points")
    public ResponseVO<HazardPointVO> bindPoint(@PathVariable Long id,
                                               @RequestParam Long tenantId,
                                               @Valid @RequestBody HazardPointRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(pointService.bindPoint(tenantId, id, request, operator(userId, username, operator)));
    }

    @DeleteMapping("/{id}/points/{relId}")
    public ResponseVO<Void> unbindPoint(@PathVariable Long id,
                                        @PathVariable Long relId,
                                        @RequestParam Long tenantId,
                                        @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                        @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        pointService.unbindPoint(tenantId, id, relId, operator(userId, username, operator));
        return ResponseVO.success(null);
    }

    @GetMapping("/{id}/attachments")
    public ResponseVO<List<HazardAttachmentVO>> listAttachments(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(attachmentService.listAttachments(tenantId, id));
    }

    @PostMapping("/{id}/attachments")
    public ResponseVO<HazardAttachmentVO> createAttachment(@PathVariable Long id,
                                                           @RequestParam Long tenantId,
                                                           @Valid @RequestBody HazardAttachmentRequest request,
                                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(attachmentService.createAttachment(tenantId, id, request,
                operator(userId, username, operator)));
    }

    @DeleteMapping("/{id}/attachments/{attachmentId}")
    public ResponseVO<Void> deleteAttachment(@PathVariable Long id,
                                             @PathVariable Long attachmentId,
                                             @RequestParam Long tenantId,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        attachmentService.deleteAttachment(tenantId, id, attachmentId, operator(userId, username, operator));
        return ResponseVO.success(null);
    }

    @GetMapping("/{id}/alarms")
    public ResponseVO<List<HazardAlarmSummaryVO>> listAlarms(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(hazardAlarmQueryService.listByHazard(tenantId, id));
    }

    @GetMapping("/{id}/permits")
    public ResponseVO<List<Map<String, Object>>> listPermits(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(Collections.<Map<String, Object>>emptyList());
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
