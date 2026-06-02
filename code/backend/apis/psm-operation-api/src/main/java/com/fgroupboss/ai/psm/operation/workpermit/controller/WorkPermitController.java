package com.fgroupboss.ai.psm.operation.workpermit.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.AcceptanceRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.CheckInRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.GasTestRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.MobileDraftSyncRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.MonitorRecordRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.PermitActionRequest;
import com.fgroupboss.ai.psm.operation.workpermit.model.dto.PreCheckRequest;
import com.fgroupboss.ai.psm.operation.workpermit.model.dto.RiskAnalysisRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.SafetyMeasureRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.SitePermitRequest;
import com.fgroupboss.ai.psm.operation.workpermit.model.dto.WorkPermitRequest;
import com.fgroupboss.ai.psm.operation.workpermit.model.dto.WorkPermitWorkerRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.GasTestVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HotWorkApprovalProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HotWorkApprovalTaskVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.MobileDraftSyncResultVO;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkWorkflowSummaryDTO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.MonitorRecordVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RiskAnalysisVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.SafetyMeasureVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.SiteConfirmVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.TimelineItemVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.WorkPermitHealthVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitWorkerVO;
import com.fgroupboss.ai.psm.operation.workpermit.service.WorkPermitService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
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
import java.util.List;

/**
 * 危险作业票（通用）接口。
 * <p>作业票全生命周期：建档、审批、现场许可、验收与关闭。</p>
 * <p>基础路径：{@code /api/work-permits}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits")
public class WorkPermitController {

    private final WorkPermitService workPermitService;
    /**
     * 服务健康检查。
     * <p>HTTP GET {@code /api/work-permits/health}</p>
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/health")
    public ResponseVO<WorkPermitHealthVO> health() {
        return ResponseVO.success(workPermitService.health());
    }
    /**
     * 分页查询危险作业票。
     * <p>HTTP GET {@code /api/work-permits}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param workType 作业类型编码
     * @param areaId 区域 ID
     * @param hazardId 重大危险源 ID
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<WorkPermitVO>> page(@LoginContext UserContext loginContext,
                                                                                                         @RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(required = false) String workType,
                                                       @RequestParam(required = false) Long areaId,
                                                       @RequestParam(required = false) Long hazardId,
                                                       @RequestParam(defaultValue = "1") int pageNo,
                                                       @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(workPermitService.page(loginContext.getTenantId(), keyword, status, workType, areaId, hazardId, pageNo, pageSize));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鎸夐噸澶у嵄闄╂簮鏌ヨ鍏宠仈浣滀笟绁ㄣ€?     */
    /**
     * 查询by hazard。
     * <p>HTTP GET {@code /api/work-permits/by-hazard}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param hazardId 重大危险源 ID
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/by-hazard")
    public ResponseVO<List<WorkPermitVO>> listByHazard(@LoginContext UserContext loginContext,
                                        @RequestParam Long hazardId) {
        return ResponseVO.success(workPermitService.listByHazard(loginContext.getTenantId(), hazardId));
    }
    /**
     * 查询作业票详情（含专项扩展字段）。
     * <p>HTTP GET {@code /api/work-permits/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<WorkPermitDetailVO> detail(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(workPermitService.getDetail(loginContext.getTenantId(), id));
    }
    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/work-permits}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<WorkPermitVO> create(@LoginContext UserContext loginContext,
                                                  @Valid @RequestBody WorkPermitRequest request,
                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(workPermitService.create(request, UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/work-permits/{id}}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<WorkPermitVO> update(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                           @Valid @RequestBody WorkPermitRequest request,
                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(workPermitService.update(id, request, UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 查询作业人员。
     * <p>HTTP GET {@code /api/work-permits/{id}/workers}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/workers")
    public ResponseVO<List<WorkPermitWorkerVO>> listWorkers(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(workPermitService.listWorkers(loginContext.getTenantId(), id));
    }
    /**
     * 新增作业人员或触发作业人员相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/workers}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/workers")
    public ResponseVO<WorkPermitWorkerVO> addWorker(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                      @Valid @RequestBody WorkPermitWorkerRequest request,
                                                                                                                                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(workPermitService.addWorker(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 删除作业人员。
     * <p>HTTP DELETE {@code /api/work-permits/{id}/workers/{workerId}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param workerId 承包商人员 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/{id}/workers/{workerId}")
    public ResponseVO<Void> removeWorker(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                         @PathVariable Long workerId,
                                                                                                                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        workPermitService.removeWorker(loginContext.getTenantId(), id, workerId, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success(null);
    }
    /**
     * 提交审批。
     * <p>HTTP POST {@code /api/work-permits/{id}/submit}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/submit")
    public ResponseVO<WorkPermitVO> submit(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.submit(loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 审批通过。
     * <p>HTTP POST {@code /api/work-permits/{id}/approve}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/approve")
    public ResponseVO<WorkPermitVO> approve(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                        @RequestBody(required = false) PermitActionRequest request,
                                                                                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.approve(loginContext.getTenantId(), id, enrichAction(request, loginContext.getUserId()),
                UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 新增return或触发return相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/return}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/return")
    public ResponseVO<WorkPermitVO> returnPermit(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                  @RequestBody(required = false) PermitActionRequest request,
                                                                                                                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.returnPermit(loginContext.getTenantId(), id, enrichAction(request, loginContext.getUserId()),
                UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 审批驳回。
     * <p>HTTP POST {@code /api/work-permits/{id}/reject}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/reject")
    public ResponseVO<WorkPermitVO> reject(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                      @RequestBody(required = false) PermitActionRequest request,
                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.reject(loginContext.getTenantId(), id, enrichAction(request, loginContext.getUserId()),
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鏌ヨ椋庨櫓鍒嗘瀽鍒楄〃銆?     */
    /**
     * 查询risk analysis。
     * <p>HTTP GET {@code /api/work-permits/{id}/risk-analysis}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/risk-analysis")
    public ResponseVO<List<RiskAnalysisVO>> listRiskAnalysis(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(workPermitService.listRiskAnalysis(loginContext.getTenantId(), id));
    }
    /**
     * 新增risk analysis或触发risk analysis相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/risk-analysis}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/risk-analysis")
    public ResponseVO<RiskAnalysisVO> saveRiskAnalysis(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                         @Valid @RequestBody RiskAnalysisRequest request,
                                                                                                                                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(workPermitService.saveRiskAnalysis(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 查询安全措施。
     * <p>HTTP GET {@code /api/work-permits/{id}/safety-measures}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/safety-measures")
    public ResponseVO<List<SafetyMeasureVO>> listSafetyMeasures(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(workPermitService.listSafetyMeasures(loginContext.getTenantId(), id));
    }
    /**
     * 新增安全措施或触发安全措施相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/safety-measures/{measureId}}</p>
     * @param id 作业票 ID
     * @param measureId measure ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/safety-measures/{measureId}")
    public ResponseVO<SafetyMeasureVO> confirmSafetyMeasure(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                            @PathVariable Long measureId,
                                                            @Valid @RequestBody SafetyMeasureRequest request,
                                                                                                                                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(workPermitService.confirmSafetyMeasure(loginContext.getTenantId(), id, measureId, request,
                UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 查询气体检测记录。
     * <p>HTTP GET {@code /api/work-permits/{id}/gas-tests}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/gas-tests")
    public ResponseVO<List<GasTestVO>> listGasTests(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(workPermitService.listGasTests(loginContext.getTenantId(), id));
    }
    /**
     * 新增气体检测记录或触发气体检测记录相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/gas-tests}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/gas-tests")
    public ResponseVO<GasTestVO> addGasTest(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                            @Valid @RequestBody GasTestRequest request,
                                                                                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(workPermitService.addGasTest(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 新增前置校验或触发前置校验相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/pre-check}</p>
     * <p>根据 checkPoint 返回是否允许进入下一流程节点。</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/pre-check")
    public ResponseVO<PreCheckResultVO> preCheck(@PathVariable Long id, @Valid @RequestBody PreCheckRequest request) {
        return ResponseVO.success(workPermitService.preCheck(request.getTenantId(), id, request));
    }
    /**
     * 新增site permit或触发site permit相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/site-permit}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/site-permit")
    public ResponseVO<WorkPermitVO> sitePermit(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                               @Valid @RequestBody SitePermitRequest request,
                                                                                                                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(workPermitService.sitePermit(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 新增check in或触发check in相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/check-in}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/check-in")
    public ResponseVO<SiteConfirmVO> checkIn(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                             @Valid @RequestBody CheckInRequest request,
                                                                                                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(workPermitService.checkIn(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 查询monitor records。
     * <p>HTTP GET {@code /api/work-permits/{id}/monitor-records}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/monitor-records")
    public ResponseVO<List<MonitorRecordVO>> listMonitorRecords(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(workPermitService.listMonitorRecords(loginContext.getTenantId(), id));
    }
    /**
     * 新增monitor records或触发monitor records相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/monitor-records}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/monitor-records")
    public ResponseVO<MonitorRecordVO> addMonitorRecord(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                        @Valid @RequestBody MonitorRecordRequest request,
                                                                                                                                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(workPermitService.addMonitorRecord(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 暂停/挂起。
     * <p>HTTP POST {@code /api/work-permits/{id}/suspend}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/suspend")
    public ResponseVO<WorkPermitVO> suspend(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                        @RequestBody(required = false) PermitActionRequest request,
                                                                                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.suspend(loginContext.getTenantId(), id, defaultAction(request),
                UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 新增resume或触发resume相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/resume}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/resume")
    public ResponseVO<WorkPermitVO> resume(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                      @RequestBody(required = false) PermitActionRequest request,
                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.resume(loginContext.getTenantId(), id, defaultAction(request),
                UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 新增terminate或触发terminate相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/terminate}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/terminate")
    public ResponseVO<WorkPermitVO> terminate(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                            @RequestBody(required = false) PermitActionRequest request,
                                                                                                                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.terminate(loginContext.getTenantId(), id, defaultAction(request),
                UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 新增acceptance或触发acceptance相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/acceptance}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/acceptance")
    public ResponseVO<WorkPermitVO> acceptance(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                               @Valid @RequestBody AcceptanceRequest request,
                                                                                                                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(workPermitService.acceptance(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }
    /**
     * 查询timeline。
     * <p>HTTP GET {@code /api/work-permits/{id}/timeline}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/timeline")
    public ResponseVO<List<TimelineItemVO>> timeline(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(workPermitService.timeline(loginContext.getTenantId(), id));
    }
    /**
     * 新增sync或触发sync相关动作。
     * <p>HTTP POST {@code /api/work-permits/mobile/drafts/sync}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/mobile/drafts/sync")
    public ResponseVO<MobileDraftSyncResultVO> syncMobileDraft(@Valid @RequestBody MobileDraftSyncRequest request) {
        return ResponseVO.success(workPermitService.syncMobileDraft(request));
    }

    /**
     * 查询available workflows。
     * <p>HTTP GET {@code /api/work-permits/hot-work/available-workflows}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param hotWorkLevel hotWorkLevel 参数
     * @param areaId 区域 ID
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/hot-work/available-workflows")
    public ResponseVO<List<HotWorkWorkflowSummaryDTO>> listAvailableHotWorkWorkflows(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) String hotWorkLevel,
            @RequestParam(required = false) Long areaId) {
        return ResponseVO.success(workPermitService.listAvailableHotWorkWorkflows(loginContext.getTenantId(), hotWorkLevel, areaId));
    }

    /**
     * 查询progress。
     * <p>HTTP GET {@code /api/work-permits/{id}/approval/progress}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/approval/progress")
    public ResponseVO<HotWorkApprovalProgressVO> getHotWorkApprovalProgress(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(workPermitService.getHotWorkApprovalProgress(loginContext.getTenantId(), id));
    }

    /**
     * 查询mine。
     * <p>HTTP GET {@code /api/work-permits/{id}/approval/tasks/mine}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param assigneeUserId assigneeUser ID
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/approval/tasks/mine")
    public ResponseVO<List<HotWorkApprovalTaskVO>> listHotWorkApprovalTasks(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                        @RequestParam(required = false) Long assigneeUserId) {
        return ResponseVO.success(workPermitService.listHotWorkApprovalTasks(loginContext.getTenantId(), id, assigneeUserId));
    }

    private PermitActionRequest defaultAction(PermitActionRequest request) {
        return request == null ? new PermitActionRequest() : request;
    }

    private PermitActionRequest enrichAction(PermitActionRequest request, Long userId) {
        PermitActionRequest action = defaultAction(request);
        if (userId != null) {
            action.setOperatorUserId(userId);
        }
        return action;
    }

    private Long parseUserId(String userId) {
        if (!StringUtils.hasText(userId)) {
            return null;
        }
        try {
            return Long.parseLong(userId.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}

