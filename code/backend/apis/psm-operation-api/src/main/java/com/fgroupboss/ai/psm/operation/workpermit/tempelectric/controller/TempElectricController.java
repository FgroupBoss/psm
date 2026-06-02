package com.fgroupboss.ai.psm.operation.workpermit.tempelectric.controller;

import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.TempElectricDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.TempElectricFacilityRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.TempElectricInspectionRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricFacilityVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricInspectionVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.tempelectric.service.TempElectricService;
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
 * 临时用电作业专项接口。
 * <p>用电设备、接地与漏电保护等专项数据。</p>
 * <p>基础路径：{@code /api/work-permits/{id}/temporary-electric}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits/{id}/temporary-electric")
public class TempElectricController {

    private final TempElectricService tempElectricService;

    /**
     * 查询专项详情。
     * <p>HTTP GET {@code /api/work-permits/{id}/temporary-electric/detail}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/detail")
    public ResponseVO<TempElectricDetailVO> getDetail(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(tempElectricService.getDetail(loginContext.getTenantId(), id));
    }

    /**
     * 更新专项详情。
     * <p>HTTP PUT {@code /api/work-permits/{id}/temporary-electric/detail}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/detail")
    public ResponseVO<TempElectricDetailVO> saveDetail(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                       @Valid @RequestBody TempElectricDetailRequest request,
                                                                                                                                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(tempElectricService.saveDetail(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询地下设施。
     * <p>HTTP GET {@code /api/work-permits/{id}/temporary-electric/facilities}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/facilities")
    public ResponseVO<List<TempElectricFacilityVO>> listFacilities(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(tempElectricService.listFacilities(loginContext.getTenantId(), id));
    }

    /**
     * 新增地下设施或触发地下设施相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/temporary-electric/facilities}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/facilities")
    public ResponseVO<TempElectricFacilityVO> addFacility(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                        @Valid @RequestBody TempElectricFacilityRequest request,
                                                                                                                                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(tempElectricService.addFacility(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询inspections。
     * <p>HTTP GET {@code /api/work-permits/{id}/temporary-electric/inspections}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/inspections")
    public ResponseVO<List<TempElectricInspectionVO>> listInspections(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(tempElectricService.listInspections(loginContext.getTenantId(), id));
    }

    /**
     * 新增inspections或触发inspections相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/temporary-electric/inspections}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/inspections")
    public ResponseVO<TempElectricInspectionVO> addInspection(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                              @Valid @RequestBody TempElectricInspectionRequest request,
                                                                                                                                                                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(tempElectricService.addInspection(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 新增前置校验或触发前置校验相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/temporary-electric/pre-check}</p>
     * <p>所有查询与变更均按租户隔离。根据 checkPoint 返回是否允许进入下一流程节点。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param checkPoint 流程校验节点编码（如 SUBMIT、SITE_PERMIT）
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/pre-check")
    public ResponseVO<TempElectricPreCheckResultVO> preCheck(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                          @RequestParam String checkPoint) {
        return ResponseVO.success(tempElectricService.preCheck(loginContext.getTenantId(), id, checkPoint));
    }

    /**
     * 查询流程进度。
     * <p>HTTP GET {@code /api/work-permits/{id}/temporary-electric/flow-progress}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/flow-progress")
    public ResponseVO<HeightWorkFlowProgressVO> flowProgress(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(tempElectricService.getFlowProgress(loginContext.getTenantId(), id));
    }

}
