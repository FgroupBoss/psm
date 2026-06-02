package com.fgroupboss.ai.psm.operation.workpermit.lifting.controller;

import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.LiftingEquipmentCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.LiftingTrialRecordRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.LiftingWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingEquipmentCheckVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingTrialRecordVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingWorkDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.lifting.service.LiftingService;
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
 * 吊装作业专项接口。
 * <p>吊装方案、吊具检查、指挥与司索人员确认等。</p>
 * <p>基础路径：{@code /api/work-permits/{id}/lifting}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits/{id}/lifting")
public class LiftingController {

    private final LiftingService liftingService;

    /**
     * 查询专项详情。
     * <p>HTTP GET {@code /api/work-permits/{id}/lifting/detail}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/detail")
    public ResponseVO<LiftingWorkDetailVO> getDetail(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(liftingService.getDetail(loginContext.getTenantId(), id));
    }

    /**
     * 更新专项详情。
     * <p>HTTP PUT {@code /api/work-permits/{id}/lifting/detail}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/detail")
    public ResponseVO<LiftingWorkDetailVO> saveDetail(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                      @Valid @RequestBody LiftingWorkDetailRequest request,
                                                                                                                                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(liftingService.saveDetail(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询equipment checks。
     * <p>HTTP GET {@code /api/work-permits/{id}/lifting/equipment-checks}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/equipment-checks")
    public ResponseVO<List<LiftingEquipmentCheckVO>> listEquipmentChecks(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(liftingService.listEquipmentChecks(loginContext.getTenantId(), id));
    }

    /**
     * 新增equipment checks或触发equipment checks相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/lifting/equipment-checks}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/equipment-checks")
    public ResponseVO<LiftingEquipmentCheckVO> addEquipmentCheck(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                 @Valid @RequestBody LiftingEquipmentCheckRequest request,
                                                                                                                                                                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(liftingService.addEquipmentCheck(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询trial records。
     * <p>HTTP GET {@code /api/work-permits/{id}/lifting/trial-records}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/trial-records")
    public ResponseVO<List<LiftingTrialRecordVO>> listTrialRecords(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(liftingService.listTrialRecords(loginContext.getTenantId(), id));
    }

    /**
     * 新增trial records或触发trial records相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/lifting/trial-records}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/trial-records")
    public ResponseVO<LiftingTrialRecordVO> addTrialRecord(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                           @Valid @RequestBody LiftingTrialRecordRequest request,
                                                                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(liftingService.addTrialRecord(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 新增前置校验或触发前置校验相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/lifting/pre-check}</p>
     * <p>所有查询与变更均按租户隔离。根据 checkPoint 返回是否允许进入下一流程节点。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param checkPoint 流程校验节点编码（如 SUBMIT、SITE_PERMIT）
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/pre-check")
    public ResponseVO<LiftingPreCheckResultVO> preCheck(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                @RequestParam String checkPoint) {
        return ResponseVO.success(liftingService.preCheck(loginContext.getTenantId(), id, checkPoint));
    }

    /**
     * 查询流程进度。
     * <p>HTTP GET {@code /api/work-permits/{id}/lifting/flow-progress}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/flow-progress")
    public ResponseVO<HeightWorkFlowProgressVO> flowProgress(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(liftingService.getFlowProgress(loginContext.getTenantId(), id));
    }

}
