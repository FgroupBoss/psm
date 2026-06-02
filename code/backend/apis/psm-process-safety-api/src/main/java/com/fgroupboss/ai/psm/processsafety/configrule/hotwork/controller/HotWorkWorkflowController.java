package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.controller;

import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkApproverResolveDTO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowQueryDTO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowSnapshotDTO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowTemplateRequest;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo.HotWorkApproverResolveResultVO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo.HotWorkWorkflowDetailVO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo.HotWorkWorkflowSummaryVO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.service.HotWorkWorkflowService;
import lombok.RequiredArgsConstructor;
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
 * HotWorkWorkflow 模块 HTTP API。
 * <p>基础路径：{@code /api/config/hot-work/workflows}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/config/hot-work/workflows")
public class HotWorkWorkflowController {

    private final HotWorkWorkflowService hotWorkWorkflowService;

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/config/hot-work/workflows}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<HotWorkWorkflowDetailVO> create(@LoginContext UserContext loginContext,
                                                  @Valid @RequestBody HotWorkWorkflowTemplateRequest request,
                                                                                                                                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(hotWorkWorkflowService.create(request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/config/hot-work/workflows/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<HotWorkWorkflowDetailVO> update(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                      @Valid @RequestBody HotWorkWorkflowTemplateRequest request,
                                                                                                                                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(hotWorkWorkflowService.update(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/config/hot-work/workflows/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<HotWorkWorkflowDetailVO> get(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(hotWorkWorkflowService.get(loginContext.getTenantId(), id));
    }

    /**
     * 查询available。
     * <p>HTTP GET {@code /api/config/hot-work/workflows/available}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param hotWorkLevel hotWorkLevel 参数
     * @param areaId 区域 ID
     * @param status 业务状态筛选
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/available")
    public ResponseVO<List<HotWorkWorkflowSummaryVO>> available(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) String hotWorkLevel,
                                                              @RequestParam(required = false) Long areaId,
                                                              @RequestParam(required = false) String status) {
        HotWorkWorkflowQueryDTO query = new HotWorkWorkflowQueryDTO();
        query.setTenantId(loginContext.getTenantId());
        query.setHotWorkLevel(hotWorkLevel);
        query.setAreaId(areaId);
        query.setStatus(status);
        return ResponseVO.success(hotWorkWorkflowService.listAvailable(query));
    }

    /**
     * 查询snapshot。
     * <p>HTTP GET {@code /api/config/hot-work/workflows/{id}/snapshot}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param versionNo versionNo 参数
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/snapshot")
    public ResponseVO<HotWorkWorkflowSnapshotDTO> snapshot(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                      @RequestParam(required = false) Integer versionNo) {
        return ResponseVO.success(hotWorkWorkflowService.getSnapshot(loginContext.getTenantId(), id, versionNo));
    }

    /**
     * 新增resolve approvers或触发resolve approvers相关动作。
     * <p>HTTP POST {@code /api/config/hot-work/workflows/resolve-approvers}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/resolve-approvers")
    public ResponseVO<HotWorkApproverResolveResultVO> resolveApprovers(@RequestBody HotWorkApproverResolveDTO request) {
        return ResponseVO.success(hotWorkWorkflowService.resolveApprovers(request));
    }

    /**
     * 新增publish或触发publish相关动作。
     * <p>HTTP POST {@code /api/config/hot-work/workflows/{id}/publish}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/publish")
    public ResponseVO<Void> publish(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                                @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        hotWorkWorkflowService.publish(loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 新增disable或触发disable相关动作。
     * <p>HTTP POST {@code /api/config/hot-work/workflows/{id}/disable}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/disable")
    public ResponseVO<Void> disable(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                                @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        hotWorkWorkflowService.disable(loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

}
