package com.fgroupboss.ai.psm.risk.majorhazard.controller;

import com.fgroupboss.ai.psm.common.DemoInfo;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.HazardAttachmentRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.HazardPointRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.HazardInspectionPlanBindRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.HazardStatusRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.MajorHazardRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.ResponsibilityReplaceRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.RiskContextRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.HazardAlarmSummaryVO;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.HazardAttachmentVO;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.HazardPointVO;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.MajorHazardResponsibilityVO;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.MajorHazardVO;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.RiskContextVO;
import com.fgroupboss.ai.psm.risk.majorhazard.service.HazardAlarmQueryService;
import com.fgroupboss.ai.psm.risk.majorhazard.service.MajorHazardAttachmentService;
import com.fgroupboss.ai.psm.risk.majorhazard.service.MajorHazardPointService;
import com.fgroupboss.ai.psm.risk.majorhazard.service.MajorHazardService;
import com.fgroupboss.ai.psm.risk.majorhazard.service.RiskContextService;
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
 * MajorHazard 模块 HTTP API。
 * <p>基础路径：{@code /api/major-hazards}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
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

    /**
     * 服务健康检查。
     * <p>HTTP GET {@code /api/major-hazards/health}</p>
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/health")
    public ResponseVO<DemoInfo> health() {
        return ResponseVO.success(new DemoInfo("psm-major-hazard-service", "major-hazard", "1.0.0-batch5"));
    }

    /**
     * 新增risk context或触发risk context相关动作。
     * <p>HTTP POST {@code /api/major-hazards/risk-context}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/risk-context")
    public ResponseVO<RiskContextVO> riskContext(@Valid @RequestBody RiskContextRequest request) {
        return ResponseVO.success(riskContextService.query(request));
    }

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/major-hazards}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param level level 参数
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<MajorHazardVO>> page(@RequestParam Long tenantId,
                                                      @RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) String status,
                                                      @RequestParam(required = false) String level,
                                                      @RequestParam(defaultValue = "1") int pageNo,
                                                      @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(hazardService.page(tenantId, keyword, status, level, pageNo, pageSize));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/major-hazards}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<MajorHazardVO> create(@Valid @RequestBody MajorHazardRequest request,
                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardService.create(request, operator(userId, username, operator)));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/major-hazards/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<MajorHazardVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(hazardService.getById(tenantId, id));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/major-hazards/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<MajorHazardVO> update(@PathVariable Long id,
                                            @Valid @RequestBody MajorHazardRequest request,
                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardService.update(id, request, operator(userId, username, operator)));
    }

    /**
     * 新增publish或触发publish相关动作。
     * <p>HTTP POST {@code /api/major-hazards/{id}/publish}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/publish")
    public ResponseVO<MajorHazardVO> publish(@PathVariable Long id,
                                             @RequestParam Long tenantId,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardService.publish(tenantId, id, operator(userId, username, operator)));
    }

    /**
     * 新增status或触发status相关动作。
     * <p>HTTP POST {@code /api/major-hazards/{id}/status}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/status")
    public ResponseVO<MajorHazardVO> changeStatus(@PathVariable Long id,
                                                  @RequestParam Long tenantId,
                                                  @Valid @RequestBody HazardStatusRequest request,
                                                  @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                  @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardService.changeStatus(tenantId, id, request, operator(userId, username, operator)));
    }

    /**
     * 查询responsibilities。
     * <p>HTTP GET {@code /api/major-hazards/{id}/responsibilities}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/responsibilities")
    public ResponseVO<List<MajorHazardResponsibilityVO>> listResponsibilities(@PathVariable Long id,
                                                                                @RequestParam Long tenantId) {
        return ResponseVO.success(hazardService.listResponsibilities(tenantId, id));
    }

    /**
     * 更新responsibilities。
     * <p>HTTP PUT {@code /api/major-hazards/{id}/responsibilities}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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

    /**
     * 查询points。
     * <p>HTTP GET {@code /api/major-hazards/{id}/points}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/points")
    public ResponseVO<List<HazardPointVO>> listPoints(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(pointService.listPoints(tenantId, id));
    }

    /**
     * 新增points或触发points相关动作。
     * <p>HTTP POST {@code /api/major-hazards/{id}/points}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/points")
    public ResponseVO<HazardPointVO> bindPoint(@PathVariable Long id,
                                               @RequestParam Long tenantId,
                                               @Valid @RequestBody HazardPointRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(pointService.bindPoint(tenantId, id, request, operator(userId, username, operator)));
    }

    /**
     * 删除points。
     * <p>HTTP DELETE {@code /api/major-hazards/{id}/points/{relId}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param relId rel ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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

    /**
     * 查询attachments。
     * <p>HTTP GET {@code /api/major-hazards/{id}/attachments}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/attachments")
    public ResponseVO<List<HazardAttachmentVO>> listAttachments(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(attachmentService.listAttachments(tenantId, id));
    }

    /**
     * 新增attachments或触发attachments相关动作。
     * <p>HTTP POST {@code /api/major-hazards/{id}/attachments}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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

    /**
     * 删除attachments。
     * <p>HTTP DELETE {@code /api/major-hazards/{id}/attachments/{attachmentId}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param attachmentId attachment ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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

    /**
     * 查询alarms。
     * <p>HTTP GET {@code /api/major-hazards/{id}/alarms}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/alarms")
    public ResponseVO<List<HazardAlarmSummaryVO>> listAlarms(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(hazardAlarmQueryService.listByHazard(tenantId, id));
    }

    /**
     * 新增bind或触发bind相关动作。
     * <p>HTTP POST {@code /api/major-hazards/{id}/inspection-plan/bind}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/inspection-plan/bind")
    public ResponseVO<MajorHazardVO> bindInspectionPlan(@PathVariable Long id,
                                                        @Valid @RequestBody HazardInspectionPlanBindRequest request,
                                                        @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                        @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(hazardService.bindInspectionPlan(
                request.getTenantId(), id, request.getInspectionPlanId(),
                operator(userId, username, operator)));
    }

    /**
     * 查询permits。
     * <p>HTTP GET {@code /api/major-hazards/{id}/permits}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/permits")
    public ResponseVO<List<Map<String, Object>>> listPermits(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(Collections.<Map<String, Object>>emptyList());
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
