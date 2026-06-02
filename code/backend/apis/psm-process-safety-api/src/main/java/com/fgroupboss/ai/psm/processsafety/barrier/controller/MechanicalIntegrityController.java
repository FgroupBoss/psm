package com.fgroupboss.ai.psm.processsafety.barrier.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.MiDefectCloseRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.MiDefectRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.MiEquipmentRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.MiInspectionPlanRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.vo.MiDefectVO;
import com.fgroupboss.ai.psm.processsafety.barrier.model.vo.MiEquipmentVO;
import com.fgroupboss.ai.psm.processsafety.barrier.model.vo.MiInspectionPlanVO;
import com.fgroupboss.ai.psm.processsafety.barrier.service.MechanicalIntegrityService;
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

/**
 * MechanicalIntegrity 模块 HTTP API。
 * <p>基础路径：{@code /api/mechanical-integrity}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mechanical-integrity")
public class MechanicalIntegrityController {

    private final MechanicalIntegrityService mechanicalIntegrityService;

    /**
     * 查询equipment。
     * <p>HTTP GET {@code /api/mechanical-integrity/equipment}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/equipment")
    public ResponseVO<PageResult<MiEquipmentVO>> pageEquipment(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) String keyword,
                                                               @RequestParam(defaultValue = "1") int pageNo,
                                                               @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(mechanicalIntegrityService.pageEquipment(loginContext.getTenantId(), keyword, pageNo, pageSize));
    }

    /**
     * 新增equipment或触发equipment相关动作。
     * <p>HTTP POST {@code /api/mechanical-integrity/equipment}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/equipment")
    public ResponseVO<MiEquipmentVO> createEquipment(@Valid @RequestBody MiEquipmentRequest request,
                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mechanicalIntegrityService.createEquipment(request, operator));
    }

    /**
     * 更新equipment。
     * <p>HTTP PUT {@code /api/mechanical-integrity/equipment/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/equipment/{id}")
    public ResponseVO<MiEquipmentVO> updateEquipment(@PathVariable Long id,
                                                     @Valid @RequestBody MiEquipmentRequest request,
                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mechanicalIntegrityService.updateEquipment(id, request, operator));
    }

    /**
     * 查询inspection plans。
     * <p>HTTP GET {@code /api/mechanical-integrity/inspection-plans}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param equipmentId equipment ID
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/inspection-plans")
    public ResponseVO<PageResult<MiInspectionPlanVO>> pageInspectionPlans(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) Long equipmentId,
                                                                          @RequestParam(defaultValue = "1") int pageNo,
                                                                          @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(mechanicalIntegrityService.pageInspectionPlans(loginContext.getTenantId(), equipmentId, pageNo, pageSize));
    }

    /**
     * 新增inspection plans或触发inspection plans相关动作。
     * <p>HTTP POST {@code /api/mechanical-integrity/inspection-plans}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/inspection-plans")
    public ResponseVO<MiInspectionPlanVO> createInspectionPlan(@Valid @RequestBody MiInspectionPlanRequest request,
                                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mechanicalIntegrityService.createInspectionPlan(request, operator));
    }

    /**
     * 查询defects。
     * <p>HTTP GET {@code /api/mechanical-integrity/defects}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param equipmentId equipment ID
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/defects")
    public ResponseVO<PageResult<MiDefectVO>> pageDefects(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) Long equipmentId,
                                                        @RequestParam(required = false) String status,
                                                        @RequestParam(defaultValue = "1") int pageNo,
                                                        @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(mechanicalIntegrityService.pageDefects(loginContext.getTenantId(), equipmentId, status, pageNo, pageSize));
    }

    /**
     * 新增defects或触发defects相关动作。
     * <p>HTTP POST {@code /api/mechanical-integrity/defects}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/defects")
    public ResponseVO<MiDefectVO> createDefect(@Valid @RequestBody MiDefectRequest request,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mechanicalIntegrityService.createDefect(request, operator));
    }

    /**
     * 新增close或触发close相关动作。
     * <p>HTTP POST {@code /api/mechanical-integrity/defects/{id}/close}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/defects/{id}/close")
    public ResponseVO<MiDefectVO> closeDefect(@PathVariable Long id,
                                              @Valid @RequestBody MiDefectCloseRequest request,
                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mechanicalIntegrityService.closeDefect(id, request, operator));
    }
}
