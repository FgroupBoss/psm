package com.fgroupboss.ai.psm.processsafety.pha.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.LopaScenarioRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.LopaCalculateResultVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.LopaScenarioVO;
import com.fgroupboss.ai.psm.processsafety.pha.service.LopaScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * LopaScenario 模块 HTTP API。
 * <p>工艺危害分析（PHA/HAZOP/LOPA）项目与节点数据。</p>
 * <p>基础路径：{@code /api/pha/lopa-scenarios}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pha/lopa-scenarios")
public class LopaScenarioController {

    private final LopaScenarioService lopaScenarioService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/pha/lopa-scenarios}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param projectId project ID
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<LopaScenarioVO>> page(@LoginContext UserContext loginContext,
                                                                                                           @RequestParam(required = false) Long projectId,
                                                         @RequestParam(defaultValue = "1") int pageNo,
                                                         @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(lopaScenarioService.page(loginContext.getTenantId(), projectId, pageNo, pageSize));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/pha/lopa-scenarios}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<LopaScenarioVO> create(@Valid @RequestBody LopaScenarioRequest request,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(lopaScenarioService.create(request, operator));
    }

    /**
     * 新增calculate或触发calculate相关动作。
     * <p>HTTP POST {@code /api/pha/lopa-scenarios/{id}/calculate}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/calculate")
    public ResponseVO<LopaCalculateResultVO> calculate(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(lopaScenarioService.calculate(id, loginContext.getTenantId()));
    }
}
