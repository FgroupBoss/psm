package com.fgroupboss.ai.psm.identity.iam.controller;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.identity.iam.model.dto.AssignDataScopeRequest;
import com.fgroupboss.ai.psm.identity.iam.model.dto.AssignRolePermissionRequest;
import com.fgroupboss.ai.psm.identity.iam.model.dto.AssignUserRoleRequest;
import com.fgroupboss.ai.psm.identity.iam.model.dto.IamUserRequest;
import com.fgroupboss.ai.psm.identity.iam.model.dto.MenuResourceRequest;
import com.fgroupboss.ai.psm.identity.iam.model.dto.OrgRequest;
import com.fgroupboss.ai.psm.identity.iam.model.dto.PostRequest;
import com.fgroupboss.ai.psm.identity.iam.model.dto.RoleRequest;
import com.fgroupboss.ai.psm.identity.iam.model.dto.TenantRequest;
import com.fgroupboss.ai.psm.identity.iam.model.dto.UserStatusRequest;
import com.fgroupboss.ai.psm.identity.iam.model.vo.IamUserVO;
import com.fgroupboss.ai.psm.identity.iam.model.vo.MenuTreeVO;
import com.fgroupboss.ai.psm.identity.iam.model.vo.OrgTreeVO;
import com.fgroupboss.ai.psm.identity.iam.model.vo.PostVO;
import com.fgroupboss.ai.psm.identity.iam.model.vo.RoleVO;
import com.fgroupboss.ai.psm.identity.iam.model.vo.TenantVO;
import com.fgroupboss.ai.psm.identity.iam.model.vo.UserPermissionSummaryVO;
import com.fgroupboss.ai.psm.identity.iam.service.IamAdminService;
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
import java.util.List;

/**
 * IamAdmin 模块 HTTP API。
 * <p>基础路径：{@code /api/iam}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/iam")
public class IamAdminController {

    private final IamAdminService service;

    /**
     * 查询tenants。
     * <p>HTTP GET {@code /api/iam/tenants}</p>
     * @param keyword 模糊搜索关键字
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/tenants")
    public ResponseVO<PageResult<TenantVO>> tenants(@RequestParam(required = false) String keyword,
                                                    @RequestParam(defaultValue = "1") int pageNo,
                                                    @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.pageTenants(keyword, pageNo, pageSize));
    }

    /**
     * 新增tenants或触发tenants相关动作。
     * <p>HTTP POST {@code /api/iam/tenants}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/tenants")
    public ResponseVO<TenantVO> createTenant(@LoginContext UserContext loginContext,
                                        @Valid @RequestBody TenantRequest request,
                                                                                                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.createTenant(request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新tenants。
     * <p>HTTP PUT {@code /api/iam/tenants/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/tenants/{id}")
    public ResponseVO<TenantVO> updateTenant(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                             @Valid @RequestBody TenantRequest request,
                                                                                                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.updateTenant(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新status。
     * <p>HTTP PUT {@code /api/iam/tenants/{id}/status}</p>
     * @param id 资源主键 ID
     * @param status 业务状态筛选
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/tenants/{id}/status")
    public ResponseVO<Void> updateTenantStatus(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                               @RequestParam String status,
                                                                                                                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.updateTenantStatus(id, status, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 查询tree。
     * <p>HTTP GET {@code /api/iam/orgs/tree}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/orgs/tree")
    public ResponseVO<List<OrgTreeVO>> orgTree(@LoginContext UserContext loginContext) {
        return ResponseVO.success(service.orgTree(loginContext.getTenantId()));
    }

    /**
     * 新增orgs或触发orgs相关动作。
     * <p>HTTP POST {@code /api/iam/orgs}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/orgs")
    public ResponseVO<OrgTreeVO> createOrg(@LoginContext UserContext loginContext,
                                        @Valid @RequestBody OrgRequest request,
                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.createOrg(request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新orgs。
     * <p>HTTP PUT {@code /api/iam/orgs/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/orgs/{id}")
    public ResponseVO<OrgTreeVO> updateOrg(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                           @Valid @RequestBody OrgRequest request,
                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.updateOrg(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 删除orgs。
     * <p>HTTP DELETE {@code /api/iam/orgs/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/orgs/{id}")
    public ResponseVO<Void> deleteOrg(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.deleteOrg(loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 查询posts。
     * <p>HTTP GET {@code /api/iam/posts}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/posts")
    public ResponseVO<PageResult<PostVO>> posts(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) String keyword,
                                                @RequestParam(defaultValue = "1") int pageNo,
                                                @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.pagePosts(loginContext.getTenantId(), keyword, pageNo, pageSize));
    }

    /**
     * 新增posts或触发posts相关动作。
     * <p>HTTP POST {@code /api/iam/posts}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/posts")
    public ResponseVO<PostVO> createPost(@LoginContext UserContext loginContext,
                                        @Valid @RequestBody PostRequest request,
                                                                                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.createPost(request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新posts。
     * <p>HTTP PUT {@code /api/iam/posts/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/posts/{id}")
    public ResponseVO<PostVO> updatePost(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                         @Valid @RequestBody PostRequest request,
                                                                                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.updatePost(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 删除posts。
     * <p>HTTP DELETE {@code /api/iam/posts/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/posts/{id}")
    public ResponseVO<Void> deletePost(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.deletePost(loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 查询users。
     * <p>HTTP GET {@code /api/iam/users}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/users")
    public ResponseVO<PageResult<IamUserVO>> users(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) String keyword,
                                                   @RequestParam(defaultValue = "1") int pageNo,
                                                   @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.pageUsers(loginContext.getTenantId(), keyword, pageNo, pageSize));
    }

    /**
     * 新增users或触发users相关动作。
     * <p>HTTP POST {@code /api/iam/users}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/users")
    public ResponseVO<IamUserVO> createUser(@LoginContext UserContext loginContext,
                                        @Valid @RequestBody IamUserRequest request,
                                                                                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.createUser(request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新users。
     * <p>HTTP PUT {@code /api/iam/users/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/users/{id}")
    public ResponseVO<IamUserVO> updateUser(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                            @Valid @RequestBody IamUserRequest request,
                                                                                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.updateUser(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新status。
     * <p>HTTP PUT {@code /api/iam/users/{id}/status}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/users/{id}/status")
    public ResponseVO<Void> updateUserStatus(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                             @Valid @RequestBody UserStatusRequest request,
                                                                                                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        service.updateUserStatus(id, request, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 更新roles。
     * <p>HTTP PUT {@code /api/iam/users/{id}/roles}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/users/{id}/roles")
    public ResponseVO<Void> assignUserRoles(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                            @Valid @RequestBody AssignUserRoleRequest request,
                                                                                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        service.assignUserRoles(id, request, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 查询permissions。
     * <p>HTTP GET {@code /api/iam/users/{id}/permissions}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/users/{id}/permissions")
    public ResponseVO<UserPermissionSummaryVO> userPermissions(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(service.userPermissions(loginContext.getTenantId(), id));
    }

    /**
     * 查询permissions。
     * <p>HTTP GET {@code /api/iam/users/me/permissions}</p>
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/users/me/permissions")
    public ResponseVO<UserPermissionSummaryVO> myPermissions(@LoginContext UserContext loginContext) {
        Long resolvedTenantId = loginContext.getTenantId();
        Long resolvedAuthUserId = loginContext.getUserId();
        if (resolvedTenantId == null || resolvedTenantId <= 0L) {
            throw new BusinessException(400, "loginContext.getTenantId() is required");
        }
        return ResponseVO.success(service.userPermissionsForPrincipal(resolvedTenantId, resolvedAuthUserId, loginContext.getUsername()));
    }

    /**
     * 查询roles。
     * <p>HTTP GET {@code /api/iam/roles}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/roles")
    public ResponseVO<PageResult<RoleVO>> roles(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) String keyword,
                                                @RequestParam(defaultValue = "1") int pageNo,
                                                @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.pageRoles(loginContext.getTenantId(), keyword, pageNo, pageSize));
    }

    /**
     * 新增roles或触发roles相关动作。
     * <p>HTTP POST {@code /api/iam/roles}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/roles")
    public ResponseVO<RoleVO> createRole(@LoginContext UserContext loginContext,
                                        @Valid @RequestBody RoleRequest request,
                                                                                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.createRole(request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新roles。
     * <p>HTTP PUT {@code /api/iam/roles/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/roles/{id}")
    public ResponseVO<RoleVO> updateRole(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                         @Valid @RequestBody RoleRequest request,
                                                                                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.updateRole(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 删除roles。
     * <p>HTTP DELETE {@code /api/iam/roles/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/roles/{id}")
    public ResponseVO<Void> deleteRole(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.deleteRole(loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 更新permissions。
     * <p>HTTP PUT {@code /api/iam/roles/{id}/permissions}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/roles/{id}/permissions")
    public ResponseVO<Void> assignRolePermissions(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                  @Valid @RequestBody AssignRolePermissionRequest request,
                                                                                                                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        service.assignRolePermissions(id, request, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 更新data scopes。
     * <p>HTTP PUT {@code /api/iam/roles/{id}/data-scopes}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/roles/{id}/data-scopes")
    public ResponseVO<Void> assignDataScope(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                            @Valid @RequestBody AssignDataScopeRequest request,
                                                                                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        service.assignDataScope(id, request, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 查询tree。
     * <p>HTTP GET {@code /api/iam/menus/tree}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/menus/tree")
    public ResponseVO<List<MenuTreeVO>> menuTree(@LoginContext UserContext loginContext) {
        return ResponseVO.success(service.menuTree(loginContext.getTenantId()));
    }

    /**
     * 新增menus或触发menus相关动作。
     * <p>HTTP POST {@code /api/iam/menus}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/menus")
    public ResponseVO<MenuTreeVO> createMenu(@LoginContext UserContext loginContext,
                                        @Valid @RequestBody MenuResourceRequest request,
                                                                                                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.createMenu(request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新menus。
     * <p>HTTP PUT {@code /api/iam/menus/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/menus/{id}")
    public ResponseVO<MenuTreeVO> updateMenu(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                             @Valid @RequestBody MenuResourceRequest request,
                                                                                                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.updateMenu(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 删除menus。
     * <p>HTTP DELETE {@code /api/iam/menus/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/menus/{id}")
    public ResponseVO<Void> deleteMenu(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.deleteMenu(loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

}
