package com.fgroupboss.ai.psm.operation.workpermit.heightwork.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkEnvironmentCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkHazardFactorRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkProtectionCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkEnvironmentCheckVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkHazardFactorVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkProtectionCheckVO;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.service.HeightWorkService;
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
 * 高处作业专项接口。
 * <p>高处作业危险因素、防护/环境检查及流程进度。</p>
 * <p>基础路径：{@code /api/work-permits/{id}/height-work}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits/{id}/height-work")
public class HeightWorkController {

    private final HeightWorkService heightWorkService;

    /**
     * 查询专项详情。
     * <p>HTTP GET {@code /api/work-permits/{id}/height-work/detail}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/detail")
    public ResponseVO<HeightWorkDetailVO> getDetail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(heightWorkService.getDetail(tenantId, id));
    }

    /**
     * 更新专项详情。
     * <p>HTTP PUT {@code /api/work-permits/{id}/height-work/detail}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/detail")
    public ResponseVO<HeightWorkDetailVO> saveDetail(@PathVariable Long id,
                                                     @Valid @RequestBody HeightWorkDetailRequest request,
                                                     @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                     @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(heightWorkService.saveDetail(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    /**
     * 查询危险因素。
     * <p>HTTP GET {@code /api/work-permits/{id}/height-work/hazard-factors}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/hazard-factors")
    public ResponseVO<List<HeightWorkHazardFactorVO>> listHazardFactors(@PathVariable Long id,
                                                                        @RequestParam Long tenantId) {
        return ResponseVO.success(heightWorkService.listHazardFactors(tenantId, id));
    }

    /**
     * 新增危险因素或触发危险因素相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/height-work/hazard-factors}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/hazard-factors")
    public ResponseVO<HeightWorkHazardFactorVO> addHazardFactor(@PathVariable Long id,
                                                                  @Valid @RequestBody HeightWorkHazardFactorRequest request,
                                                                  @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                  @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(heightWorkService.addHazardFactor(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    /**
     * 新增confirm或触发confirm相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/height-work/hazard-factors/{factorId}/confirm}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param factorId 危险因素记录 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/hazard-factors/{factorId}/confirm")
    public ResponseVO<HeightWorkHazardFactorVO> confirmHazardFactor(@PathVariable Long id,
                                                                    @PathVariable Long factorId,
                                                                    @RequestParam Long tenantId,
                                                                    @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                    @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(heightWorkService.confirmHazardFactor(tenantId, id, factorId,
                operator(userId, username, operator)));
    }

    /**
     * 查询防护措施检查。
     * <p>HTTP GET {@code /api/work-permits/{id}/height-work/protection-checks}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param checkStage checkStage 参数
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/protection-checks")
    public ResponseVO<List<HeightWorkProtectionCheckVO>> listProtectionChecks(@PathVariable Long id,
                                                                              @RequestParam Long tenantId,
                                                                              @RequestParam(required = false) String checkStage) {
        return ResponseVO.success(heightWorkService.listProtectionChecks(tenantId, id, checkStage));
    }

    /**
     * 新增防护措施检查或触发防护措施检查相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/height-work/protection-checks}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/protection-checks")
    public ResponseVO<HeightWorkProtectionCheckVO> addProtectionCheck(@PathVariable Long id,
                                                                      @Valid @RequestBody HeightWorkProtectionCheckRequest request,
                                                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                      @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(heightWorkService.addProtectionCheck(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    /**
     * 查询作业环境检查。
     * <p>HTTP GET {@code /api/work-permits/{id}/height-work/environment-checks}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param checkStage checkStage 参数
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/environment-checks")
    public ResponseVO<List<HeightWorkEnvironmentCheckVO>> listEnvironmentChecks(@PathVariable Long id,
                                                                                @RequestParam Long tenantId,
                                                                                @RequestParam(required = false) String checkStage) {
        return ResponseVO.success(heightWorkService.listEnvironmentChecks(tenantId, id, checkStage));
    }

    /**
     * 新增作业环境检查或触发作业环境检查相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/height-work/environment-checks}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/environment-checks")
    public ResponseVO<HeightWorkEnvironmentCheckVO> addEnvironmentCheck(@PathVariable Long id,
                                                                        @Valid @RequestBody HeightWorkEnvironmentCheckRequest request,
                                                                        @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                        @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(heightWorkService.addEnvironmentCheck(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    /**
     * 按规则重新计算派生字段。
     * <p>HTTP POST {@code /api/work-permits/{id}/height-work/recalculate}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/recalculate")
    public ResponseVO<HeightWorkDetailVO> recalculate(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(heightWorkService.recalculate(tenantId, id));
    }

    /**
     * 新增前置校验或触发前置校验相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/height-work/pre-check}</p>
     * <p>所有查询与变更均按租户隔离。根据 checkPoint 返回是否允许进入下一流程节点。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param checkPoint 流程校验节点编码（如 SUBMIT、SITE_PERMIT）
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/pre-check")
    public ResponseVO<HeightWorkPreCheckResultVO> preCheck(@PathVariable Long id,
                                                           @RequestParam Long tenantId,
                                                           @RequestParam String checkPoint) {
        return ResponseVO.success(heightWorkService.preCheck(tenantId, id, checkPoint));
    }

    /**
     * 查询流程进度。
     * <p>HTTP GET {@code /api/work-permits/{id}/height-work/flow-progress}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/flow-progress")
    public ResponseVO<HeightWorkFlowProgressVO> flowProgress(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(heightWorkService.getFlowProgress(tenantId, id));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
