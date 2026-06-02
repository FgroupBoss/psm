package com.fgroupboss.ai.psm.operation.workpermit.confinedspace.controller;

import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ConfinedSpaceDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ConfinedSpaceEntryRecordRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ConfinedSpaceRescuePlanRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpaceDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpaceEntryRecordVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpacePreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpaceRescuePlanVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.workpermit.confinedspace.service.ConfinedSpaceService;
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
 * 受限空间作业专项接口。
 * <p>受限空间隔离、气体检测与人员进出登记。</p>
 * <p>基础路径：{@code /api/work-permits/{id}/confined-space}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits/{id}/confined-space")
public class ConfinedSpaceController {

    private final ConfinedSpaceService confinedSpaceService;

    /**
     * 查询专项详情。
     * <p>HTTP GET {@code /api/work-permits/{id}/confined-space/detail}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/detail")
    public ResponseVO<ConfinedSpaceDetailVO> getDetail(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(confinedSpaceService.getDetail(loginContext.getTenantId(), id));
    }

    /**
     * 更新专项详情。
     * <p>HTTP PUT {@code /api/work-permits/{id}/confined-space/detail}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/detail")
    public ResponseVO<ConfinedSpaceDetailVO> saveDetail(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                        @Valid @RequestBody ConfinedSpaceDetailRequest request,
                                                                                                                                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(confinedSpaceService.saveDetail(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询entries。
     * <p>HTTP GET {@code /api/work-permits/{id}/confined-space/entries}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/entries")
    public ResponseVO<List<ConfinedSpaceEntryRecordVO>> listEntries(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(confinedSpaceService.listEntryRecords(loginContext.getTenantId(), id));
    }

    /**
     * 新增entries或触发entries相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/confined-space/entries}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/entries")
    public ResponseVO<ConfinedSpaceEntryRecordVO> addEntry(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                           @Valid @RequestBody ConfinedSpaceEntryRecordRequest request,
                                                                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(confinedSpaceService.addEntryRecord(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询rescue plan。
     * <p>HTTP GET {@code /api/work-permits/{id}/confined-space/rescue-plan}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/rescue-plan")
    public ResponseVO<ConfinedSpaceRescuePlanVO> getRescuePlan(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(confinedSpaceService.getRescuePlan(loginContext.getTenantId(), id));
    }

    /**
     * 更新rescue plan。
     * <p>HTTP PUT {@code /api/work-permits/{id}/confined-space/rescue-plan}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/rescue-plan")
    public ResponseVO<ConfinedSpaceRescuePlanVO> saveRescuePlan(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                @Valid @RequestBody ConfinedSpaceRescuePlanRequest request,
                                                                                                                                                                                                @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(confinedSpaceService.saveRescuePlan(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 新增前置校验或触发前置校验相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/confined-space/pre-check}</p>
     * <p>所有查询与变更均按租户隔离。根据 checkPoint 返回是否允许进入下一流程节点。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param checkPoint 流程校验节点编码（如 SUBMIT、SITE_PERMIT）
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/pre-check")
    public ResponseVO<ConfinedSpacePreCheckResultVO> preCheck(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                            @RequestParam String checkPoint) {
        return ResponseVO.success(confinedSpaceService.preCheck(loginContext.getTenantId(), id, checkPoint));
    }

    /**
     * 查询流程进度。
     * <p>HTTP GET {@code /api/work-permits/{id}/confined-space/flow-progress}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/flow-progress")
    public ResponseVO<HeightWorkFlowProgressVO> flowProgress(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(confinedSpaceService.getFlowProgress(loginContext.getTenantId(), id));
    }

}
