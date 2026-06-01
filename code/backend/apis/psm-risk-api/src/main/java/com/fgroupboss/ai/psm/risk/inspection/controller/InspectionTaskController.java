package com.fgroupboss.ai.psm.risk.inspection.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.OverdueScanRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.TaskAbnormalRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.TaskCreateRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.TaskDraftSyncRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.TaskItemSubmitRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.TaskSignInRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.TaskStartRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.AbnormalRecordVO;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.InspectionStatisticsVO;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.InspectionTaskVO;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.OverdueScanResultVO;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.SignRecordVO;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.TaskDraftSyncResultVO;
import com.fgroupboss.ai.psm.risk.inspection.service.InspTaskDraftService;
import com.fgroupboss.ai.psm.risk.inspection.service.InspectionTaskService;
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
 * InspectionTask 模块 HTTP API。
 * <p>巡检计划、路线、任务与检查表。</p>
 * <p>基础路径：{@code /api/inspection/tasks}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inspection/tasks")
public class InspectionTaskController {

    private final InspectionTaskService taskService;
    private final InspTaskDraftService draftService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/inspection/tasks}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param status 业务状态筛选
     * @param executorId executor ID
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<InspectionTaskVO>> page(@RequestParam Long tenantId,
                                                           @RequestParam(required = false) String status,
                                                           @RequestParam(required = false) Long executorId,
                                                           @RequestParam(defaultValue = "1") int pageNo,
                                                           @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(taskService.page(tenantId, status, executorId, pageNo, pageSize));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/inspection/tasks/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<InspectionTaskVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(taskService.getById(tenantId, id));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/inspection/tasks}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<InspectionTaskVO> create(@Valid @RequestBody TaskCreateRequest request) {
        return ResponseVO.success(taskService.create(request));
    }

    /**
     * 新增start或触发start相关动作。
     * <p>HTTP POST {@code /api/inspection/tasks/{id}/start}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/start")
    public ResponseVO<InspectionTaskVO> start(@PathVariable Long id, @Valid @RequestBody TaskStartRequest request) {
        return ResponseVO.success(taskService.start(id, request));
    }

    /**
     * 新增sign in或触发sign in相关动作。
     * <p>HTTP POST {@code /api/inspection/tasks/{id}/sign-in}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/sign-in")
    public ResponseVO<SignRecordVO> signIn(@PathVariable Long id, @Valid @RequestBody TaskSignInRequest request) {
        return ResponseVO.success(taskService.signIn(id, request));
    }

    /**
     * 更新items。
     * <p>HTTP PUT {@code /api/inspection/tasks/{id}/items}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}/items")
    public ResponseVO<InspectionTaskVO> submitItems(@PathVariable Long id,
                                                    @Valid @RequestBody TaskItemSubmitRequest request) {
        return ResponseVO.success(taskService.submitItems(id, request));
    }

    /**
     * 新增complete或触发complete相关动作。
     * <p>HTTP POST {@code /api/inspection/tasks/{id}/complete}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/complete")
    public ResponseVO<InspectionTaskVO> complete(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(taskService.complete(tenantId, id));
    }

    /**
     * 新增abnormals或触发abnormals相关动作。
     * <p>HTTP POST {@code /api/inspection/tasks/{id}/abnormals}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/abnormals")
    public ResponseVO<AbnormalRecordVO> registerAbnormal(@PathVariable Long id,
                                                         @Valid @RequestBody TaskAbnormalRequest request) {
        return ResponseVO.success(taskService.registerAbnormal(id, request));
    }

    /**
     * 新增overdue scan或触发overdue scan相关动作。
     * <p>HTTP POST {@code /api/inspection/tasks/overdue-scan}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/overdue-scan")
    public ResponseVO<OverdueScanResultVO> overdueScan(@Valid @RequestBody OverdueScanRequest request) {
        return ResponseVO.success(taskService.overdueScan(request));
    }

    /**
     * 新增draft sync或触发draft sync相关动作。
     * <p>HTTP POST {@code /api/inspection/tasks/draft-sync}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/draft-sync")
    public ResponseVO<TaskDraftSyncResultVO> syncDraft(@Valid @RequestBody TaskDraftSyncRequest request) {
        return ResponseVO.success(draftService.syncDraft(request));
    }

    /**
     * 查询by major hazard。
     * <p>HTTP GET {@code /api/inspection/tasks/by-major-hazard}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param majorHazardId majorHazard ID
     * @param limit limit 参数
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/by-major-hazard")
    public ResponseVO<List<InspectionTaskVO>> listByMajorHazard(@RequestParam Long tenantId,
                                                                  @RequestParam Long majorHazardId,
                                                                  @RequestParam(defaultValue = "10") int limit) {
        return ResponseVO.success(taskService.listRecentByMajorHazard(tenantId, majorHazardId, limit));
    }

    /**
     * 查询statistics。
     * <p>HTTP GET {@code /api/inspection/tasks/statistics}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param from from 参数
     * @param to to 参数
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/statistics")
    public ResponseVO<InspectionStatisticsVO> statistics(
            @RequestParam Long tenantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseVO.success(taskService.statistics(tenantId, from, to));
    }
}
