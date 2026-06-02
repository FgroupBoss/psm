package com.fgroupboss.ai.psm.operation.workpermit.blindplate.controller;

import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.BlindPlateActionConfirmRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.BlindPlateWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateActionRecordVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlatePreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateWorkDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.blindplate.service.BlindPlateService;
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
 * 盲板抽堵作业专项接口。
 * <p>盲板位置、隔离措施及作业确认。</p>
 * <p>基础路径：{@code /api/work-permits/{id}/blind-plate}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits/{id}/blind-plate")
public class BlindPlateController {

    private final BlindPlateService blindPlateService;

    /**
     * 查询专项详情。
     * <p>HTTP GET {@code /api/work-permits/{id}/blind-plate/detail}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/detail")
    public ResponseVO<BlindPlateWorkDetailVO> getDetail(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(blindPlateService.getDetail(loginContext.getTenantId(), id));
    }

    /**
     * 更新专项详情。
     * <p>HTTP PUT {@code /api/work-permits/{id}/blind-plate/detail}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/detail")
    public ResponseVO<BlindPlateWorkDetailVO> saveDetail(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                         @Valid @RequestBody BlindPlateWorkDetailRequest request,
                                                                                                                                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(blindPlateService.saveDetail(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询action records。
     * <p>HTTP GET {@code /api/work-permits/{id}/blind-plate/action-records}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/action-records")
    public ResponseVO<List<BlindPlateActionRecordVO>> listActionRecords(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(blindPlateService.listActionRecords(loginContext.getTenantId(), id));
    }

    /**
     * 新增action confirm或触发action confirm相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/blind-plate/action-confirm}</p>
     * @param id 作业票 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/action-confirm")
    public ResponseVO<BlindPlateActionRecordVO> confirmAction(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                @Valid @RequestBody BlindPlateActionConfirmRequest request,
                                                                                                                                                                                                @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(blindPlateService.confirmAction(loginContext.getTenantId(), id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 新增前置校验或触发前置校验相关动作。
     * <p>HTTP POST {@code /api/work-permits/{id}/blind-plate/pre-check}</p>
     * <p>所有查询与变更均按租户隔离。根据 checkPoint 返回是否允许进入下一流程节点。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param checkPoint 流程校验节点编码（如 SUBMIT、SITE_PERMIT）
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/pre-check")
    public ResponseVO<BlindPlatePreCheckResultVO> preCheck(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                          @RequestParam String checkPoint) {
        return ResponseVO.success(blindPlateService.preCheck(loginContext.getTenantId(), id, checkPoint));
    }

    /**
     * 查询流程进度。
     * <p>HTTP GET {@code /api/work-permits/{id}/blind-plate/flow-progress}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 作业票 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/flow-progress")
    public ResponseVO<BlindPlateFlowProgressVO> flowProgress(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(blindPlateService.getFlowProgress(loginContext.getTenantId(), id));
    }

}
