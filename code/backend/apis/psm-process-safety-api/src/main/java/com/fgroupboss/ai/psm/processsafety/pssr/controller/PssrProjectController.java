package com.fgroupboss.ai.psm.processsafety.pssr.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrApprovalRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrExecuteRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrProjectRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.vo.PssrExecutionRecordVO;
import com.fgroupboss.ai.psm.processsafety.pssr.model.vo.PssrProjectVO;
import com.fgroupboss.ai.psm.processsafety.pssr.model.vo.PssrStartupCheckVO;
import com.fgroupboss.ai.psm.processsafety.pssr.service.PssrProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * PssrProject 模块 HTTP API。
 * <p>基础路径：{@code /api/pssr/projects}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pssr/projects")
public class PssrProjectController {

    private final PssrProjectService pssrProjectService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/pssr/projects}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<PssrProjectVO>> page(@LoginContext UserContext loginContext,
                                                                                                      @RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) String status,
                                                    @RequestParam(defaultValue = "1") int pageNo,
                                                    @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(pssrProjectService.page(loginContext.getTenantId(), keyword, status, pageNo, pageSize));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/pssr/projects/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<PssrProjectVO> get(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(pssrProjectService.getById(loginContext.getTenantId(), id));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/pssr/projects}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<PssrProjectVO> create(@LoginContext UserContext loginContext,
                                                  @Valid @RequestBody PssrProjectRequest request,
                                                                                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(pssrProjectService.create(request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/pssr/projects/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<PssrProjectVO> update(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                            @Valid @RequestBody PssrProjectRequest request,
                                                                                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(pssrProjectService.update(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 删除记录。
     * <p>HTTP DELETE {@code /api/pssr/projects/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        pssrProjectService.delete(loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 新增execute或触发execute相关动作。
     * <p>HTTP POST {@code /api/pssr/projects/{id}/execute}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/execute")
    public ResponseVO<PssrExecutionRecordVO> execute(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                     @Valid @RequestBody PssrExecuteRequest request,
                                                                                                                                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(pssrProjectService.execute(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 新增approve startup或触发approve startup相关动作。
     * <p>HTTP POST {@code /api/pssr/projects/{id}/approve-startup}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/approve-startup")
    public ResponseVO<PssrProjectVO> approveStartup(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                    @Valid @RequestBody PssrApprovalRequest request,
                                                                                                                                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(pssrProjectService.approveStartup(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 新增reject startup或触发reject startup相关动作。
     * <p>HTTP POST {@code /api/pssr/projects/{id}/reject-startup}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/reject-startup")
    public ResponseVO<PssrProjectVO> rejectStartup(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                   @Valid @RequestBody PssrApprovalRequest request,
                                                                                                                                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(pssrProjectService.rejectStartup(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询startup check。
     * <p>HTTP GET {@code /api/pssr/projects/{id}/startup-check}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/startup-check")
    public ResponseVO<PssrStartupCheckVO> startupCheck(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(pssrProjectService.startupCheck(loginContext.getTenantId(), id));
    }

}
