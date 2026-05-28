package com.fgroupboss.ai.psm.inspection.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.inspection.model.dto.OverdueScanRequest;
import com.fgroupboss.ai.psm.inspection.model.dto.TaskAbnormalRequest;
import com.fgroupboss.ai.psm.inspection.model.dto.TaskCreateRequest;
import com.fgroupboss.ai.psm.inspection.model.dto.TaskDraftSyncRequest;
import com.fgroupboss.ai.psm.inspection.model.dto.TaskItemSubmitRequest;
import com.fgroupboss.ai.psm.inspection.model.dto.TaskSignInRequest;
import com.fgroupboss.ai.psm.inspection.model.dto.TaskStartRequest;
import com.fgroupboss.ai.psm.inspection.model.vo.AbnormalRecordVO;
import com.fgroupboss.ai.psm.inspection.model.vo.InspectionStatisticsVO;
import com.fgroupboss.ai.psm.inspection.model.vo.InspectionTaskVO;
import com.fgroupboss.ai.psm.inspection.model.vo.OverdueScanResultVO;
import com.fgroupboss.ai.psm.inspection.model.vo.SignRecordVO;
import com.fgroupboss.ai.psm.inspection.model.vo.TaskDraftSyncResultVO;
import com.fgroupboss.ai.psm.inspection.service.InspTaskDraftService;
import com.fgroupboss.ai.psm.inspection.service.InspectionTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 巡检任务执行与统计接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inspection/tasks")
public class InspectionTaskController {

    private final InspectionTaskService taskService;
    private final InspTaskDraftService draftService;

    @GetMapping
    public ResponseVO<PageResult<InspectionTaskVO>> page(@RequestParam Long tenantId,
                                                           @RequestParam(required = false) String status,
                                                           @RequestParam(required = false) Long executorId,
                                                           @RequestParam(defaultValue = "1") int pageNo,
                                                           @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(taskService.page(tenantId, status, executorId, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseVO<InspectionTaskVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(taskService.getById(tenantId, id));
    }

    @PostMapping
    public ResponseVO<InspectionTaskVO> create(@Valid @RequestBody TaskCreateRequest request) {
        return ResponseVO.success(taskService.create(request));
    }

    @PostMapping("/{id}/start")
    public ResponseVO<InspectionTaskVO> start(@PathVariable Long id, @Valid @RequestBody TaskStartRequest request) {
        return ResponseVO.success(taskService.start(id, request));
    }

    @PostMapping("/{id}/sign-in")
    public ResponseVO<SignRecordVO> signIn(@PathVariable Long id, @Valid @RequestBody TaskSignInRequest request) {
        return ResponseVO.success(taskService.signIn(id, request));
    }

    @PutMapping("/{id}/items")
    public ResponseVO<InspectionTaskVO> submitItems(@PathVariable Long id,
                                                    @Valid @RequestBody TaskItemSubmitRequest request) {
        return ResponseVO.success(taskService.submitItems(id, request));
    }

    @PostMapping("/{id}/complete")
    public ResponseVO<InspectionTaskVO> complete(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(taskService.complete(tenantId, id));
    }

    @PostMapping("/{id}/abnormals")
    public ResponseVO<AbnormalRecordVO> registerAbnormal(@PathVariable Long id,
                                                         @Valid @RequestBody TaskAbnormalRequest request) {
        return ResponseVO.success(taskService.registerAbnormal(id, request));
    }

    @PostMapping("/overdue-scan")
    public ResponseVO<OverdueScanResultVO> overdueScan(@Valid @RequestBody OverdueScanRequest request) {
        return ResponseVO.success(taskService.overdueScan(request));
    }

    @PostMapping("/draft-sync")
    public ResponseVO<TaskDraftSyncResultVO> syncDraft(@Valid @RequestBody TaskDraftSyncRequest request) {
        return ResponseVO.success(draftService.syncDraft(request));
    }

    @GetMapping("/by-major-hazard")
    public ResponseVO<List<InspectionTaskVO>> listByMajorHazard(@RequestParam Long tenantId,
                                                                  @RequestParam Long majorHazardId,
                                                                  @RequestParam(defaultValue = "10") int limit) {
        return ResponseVO.success(taskService.listRecentByMajorHazard(tenantId, majorHazardId, limit));
    }

    @GetMapping("/statistics")
    public ResponseVO<InspectionStatisticsVO> statistics(
            @RequestParam Long tenantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseVO.success(taskService.statistics(tenantId, from, to));
    }
}
