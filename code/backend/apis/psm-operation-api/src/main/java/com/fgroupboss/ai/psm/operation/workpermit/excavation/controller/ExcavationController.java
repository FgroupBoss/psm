package com.fgroupboss.ai.psm.operation.workpermit.excavation.controller;

import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationCountersignRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationSiteCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationUndergroundFacilityRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationCountersignVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationSiteCheckVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationUndergroundFacilityVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationWorkDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.service.ExcavationService;
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
 * 动土作业专项接口。
 * <p>挂载于作业票下的动土详情、地下设施、会签、现场检查与流程节点。</p>
 * <p>基础路径：{@code /api/work-permits/{id}/excavation}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits/{id}/excavation")
public class ExcavationController {

    private final ExcavationService excavationService;

    /**
     * 查询动土作业专项详情。
     * <p>HTTP GET {@code /api/work-permits/{id}/excavation/detail}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/detail")
    public ResponseVO<ExcavationWorkDetailVO> getDetail(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(excavationService.getDetail(loginContext.getTenantId(), id));
    }

    /**
     * 保存动土作业专项详情。
     * <p>HTTP PUT {@code /api/work-permits/{id}/excavation/detail}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/detail")
    public ResponseVO<ExcavationWorkDetailVO> saveDetail(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                           @Valid @RequestBody ExcavationWorkDetailRequest request,
                                                                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(excavationService.saveDetail(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询地下设施。
     * <p>HTTP GET {@code /api/work-permits/{id}/excavation/facilities}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/facilities")
    public ResponseVO<List<ExcavationUndergroundFacilityVO>> listFacilities(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(excavationService.listFacilities(loginContext.getTenantId(), id));
    }

    /**
     * 新增地下设施或触发地下设施相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/excavation/facilities}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/facilities")
    public ResponseVO<ExcavationUndergroundFacilityVO> addFacility(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                   @Valid @RequestBody ExcavationUndergroundFacilityRequest request,
                                                                                                                                                                                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(excavationService.addFacility(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询会签记录。
     * <p>HTTP GET {@code /api/work-permits/{id}/excavation/countersigns}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/countersigns")
    public ResponseVO<List<ExcavationCountersignVO>> listCountersigns(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(excavationService.listCountersigns(loginContext.getTenantId(), id));
    }

    /**
     * 新增会签记录或触发会签记录相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/excavation/countersigns}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/countersigns")
    public ResponseVO<ExcavationCountersignVO> addCountersign(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                              @Valid @RequestBody ExcavationCountersignRequest request,
                                                                                                                                                                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(excavationService.addCountersign(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询现场检查记录。
     * <p>HTTP GET {@code /api/work-permits/{id}/excavation/site-checks}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param stage 检查阶段（如 BEFORE、DURING、AFTER）
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/site-checks")
    public ResponseVO<List<ExcavationSiteCheckVO>> listSiteChecks(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                    @RequestParam(required = false) String stage) {
        return ResponseVO.success(excavationService.listSiteChecks(loginContext.getTenantId(), id, stage));
    }

    /**
     * 新增现场检查记录或触发现场检查记录相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/excavation/site-checks}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/site-checks")
    public ResponseVO<ExcavationSiteCheckVO> addSiteCheck(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                          @Valid @RequestBody ExcavationSiteCheckRequest request,
                                                                                                                                                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(excavationService.addSiteCheck(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 动土作业流程前置校验。
     * <p>HTTP POST {@code /api/work-permits/{id}/excavation/pre-check}</p>
     * <p>所有查询与变更均按租户隔离。根据 checkPoint 返回是否允许进入下一流程节点。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param checkPoint 流程校验节点编码（如 SUBMIT、SITE_PERMIT）
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/pre-check")
    public ResponseVO<ExcavationPreCheckResultVO> preCheck(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                        @RequestParam String checkPoint) {
        return ResponseVO.success(excavationService.preCheck(loginContext.getTenantId(), id, checkPoint));
    }

    /**
     * 查询动土作业流程进度。
     * <p>HTTP GET {@code /api/work-permits/{id}/excavation/flow-progress}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/flow-progress")
    public ResponseVO<ExcavationFlowProgressVO> flowProgress(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(excavationService.getFlowProgress(loginContext.getTenantId(), id));
    }

}
