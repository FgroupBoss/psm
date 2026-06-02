package com.fgroupboss.ai.psm.processsafety.pssr.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrIssueActionRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrIssueRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.vo.PssrIssueVO;
import com.fgroupboss.ai.psm.processsafety.pssr.service.PssrIssueService;
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
 * PssrIssue 模块 HTTP API。
 * <p>基础路径：{@code /api/pssr/issues}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pssr/issues")
public class PssrIssueController {

    private final PssrIssueService pssrIssueService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/pssr/issues}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param projectId project ID
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<PssrIssueVO>> page(@LoginContext UserContext loginContext,
                                                                                                    @RequestParam(required = false) Long projectId,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(defaultValue = "1") int pageNo,
                                                  @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(pssrIssueService.page(loginContext.getTenantId(), projectId, status, pageNo, pageSize));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/pssr/issues}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<PssrIssueVO> create(@Valid @RequestBody PssrIssueRequest request,
                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(pssrIssueService.create(request, operator));
    }

    /**
     * 新增rectify或触发rectify相关动作。
     * <p>HTTP POST {@code /api/pssr/issues/{id}/rectify}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/rectify")
    public ResponseVO<PssrIssueVO> rectify(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                           @Valid @RequestBody PssrIssueActionRequest request,
                                                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        String op = UserContextResolver.operator(loginContext, operator);
        return ResponseVO.success(pssrIssueService.rectify(id, request, op));
    }

    /**
     * 新增review或触发review相关动作。
     * <p>HTTP POST {@code /api/pssr/issues/{id}/review}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/review")
    public ResponseVO<PssrIssueVO> review(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                          @Valid @RequestBody PssrIssueActionRequest request,
                                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        String op = UserContextResolver.operator(loginContext, operator);
        return ResponseVO.success(pssrIssueService.review(id, request, op));
    }
}
