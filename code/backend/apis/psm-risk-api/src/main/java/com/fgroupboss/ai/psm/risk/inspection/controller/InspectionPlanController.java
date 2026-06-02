package com.fgroupboss.ai.psm.risk.inspection.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.InspectionPlanRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.InspectionPlanVO;
import com.fgroupboss.ai.psm.risk.inspection.service.InspectionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.Valid;

/**
 * InspectionPlan 模块 HTTP API。
 * <p>巡检计划、路线、任务与检查表。</p>
 * <p>基础路径：{@code /api/inspection/plans}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inspection/plans")
public class InspectionPlanController {

    private final InspectionPlanService planService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/inspection/plans}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<InspectionPlanVO>> page(@LoginContext UserContext loginContext,
                                                                                                           @RequestParam(required = false) String keyword,
                                                         @RequestParam(defaultValue = "1") int pageNo,
                                                         @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(planService.page(loginContext.getTenantId(), keyword, pageNo, pageSize));
    }

    /**
     * 查询by major hazard。
     * <p>HTTP GET {@code /api/inspection/plans/by-major-hazard}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param majorHazardId majorHazard ID
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/by-major-hazard")
    public ResponseVO<List<InspectionPlanVO>> listByMajorHazard(@LoginContext UserContext loginContext,
                                        @RequestParam Long majorHazardId) {
        return ResponseVO.success(planService.listByMajorHazard(loginContext.getTenantId(), majorHazardId));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/inspection/plans/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<InspectionPlanVO> get(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(planService.getById(loginContext.getTenantId(), id));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/inspection/plans}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<InspectionPlanVO> create(@Valid @RequestBody InspectionPlanRequest request) {
        return ResponseVO.success(planService.create(request));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/inspection/plans/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<InspectionPlanVO> update(@PathVariable Long id,
                                               @Valid @RequestBody InspectionPlanRequest request) {
        return ResponseVO.success(planService.update(id, request));
    }
}
