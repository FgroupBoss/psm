package com.fgroupboss.ai.psm.risk.inspection.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.ChecklistTemplateRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.ChecklistTemplateVO;
import com.fgroupboss.ai.psm.risk.inspection.service.ChecklistTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * ChecklistTemplate 模块 HTTP API。
 * <p>巡检计划、路线、任务与检查表。</p>
 * <p>基础路径：{@code /api/inspection/checklist-templates}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inspection/checklist-templates")
public class ChecklistTemplateController {

    private final ChecklistTemplateService templateService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/inspection/checklist-templates}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<ChecklistTemplateVO>> page(@LoginContext UserContext loginContext,
                                                                                                              @RequestParam(required = false) String keyword,
                                                            @RequestParam(defaultValue = "1") int pageNo,
                                                            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(templateService.page(loginContext.getTenantId(), keyword, pageNo, pageSize));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/inspection/checklist-templates/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<ChecklistTemplateVO> get(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(templateService.getById(loginContext.getTenantId(), id));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/inspection/checklist-templates}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<ChecklistTemplateVO> create(@Valid @RequestBody ChecklistTemplateRequest request) {
        return ResponseVO.success(templateService.create(request));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/inspection/checklist-templates/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<ChecklistTemplateVO> update(@PathVariable Long id,
                                                  @Valid @RequestBody ChecklistTemplateRequest request) {
        return ResponseVO.success(templateService.update(id, request));
    }
}
