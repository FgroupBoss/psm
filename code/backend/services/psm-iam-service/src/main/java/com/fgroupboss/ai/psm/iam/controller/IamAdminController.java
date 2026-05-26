package com.fgroupboss.ai.psm.iam.controller;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.iam.model.dto.AssignDataScopeRequest;
import com.fgroupboss.ai.psm.iam.model.dto.AssignRolePermissionRequest;
import com.fgroupboss.ai.psm.iam.model.dto.AssignUserRoleRequest;
import com.fgroupboss.ai.psm.iam.model.dto.IamUserRequest;
import com.fgroupboss.ai.psm.iam.model.dto.MenuResourceRequest;
import com.fgroupboss.ai.psm.iam.model.dto.OrgRequest;
import com.fgroupboss.ai.psm.iam.model.dto.PostRequest;
import com.fgroupboss.ai.psm.iam.model.dto.RoleRequest;
import com.fgroupboss.ai.psm.iam.model.dto.TenantRequest;
import com.fgroupboss.ai.psm.iam.model.dto.UserStatusRequest;
import com.fgroupboss.ai.psm.iam.model.vo.IamUserVO;
import com.fgroupboss.ai.psm.iam.model.vo.MenuTreeVO;
import com.fgroupboss.ai.psm.iam.model.vo.OrgTreeVO;
import com.fgroupboss.ai.psm.iam.model.vo.PostVO;
import com.fgroupboss.ai.psm.iam.model.vo.RoleVO;
import com.fgroupboss.ai.psm.iam.model.vo.TenantVO;
import com.fgroupboss.ai.psm.iam.model.vo.UserPermissionSummaryVO;
import com.fgroupboss.ai.psm.iam.service.IamAdminService;
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
 * IAM 后台管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/iam")
public class IamAdminController {

    private final IamAdminService service;

    @GetMapping("/tenants")
    public ResponseVO<PageResult<TenantVO>> tenants(@RequestParam(required = false) String keyword,
                                                    @RequestParam(defaultValue = "1") int pageNo,
                                                    @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.pageTenants(keyword, pageNo, pageSize));
    }

    @PostMapping("/tenants")
    public ResponseVO<TenantVO> createTenant(@Valid @RequestBody TenantRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.createTenant(request, operator(userId, username, operator)));
    }

    @PutMapping("/tenants/{id}")
    public ResponseVO<TenantVO> updateTenant(@PathVariable Long id,
                                             @Valid @RequestBody TenantRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.updateTenant(id, request, operator(userId, username, operator)));
    }

    @PutMapping("/tenants/{id}/status")
    public ResponseVO<Void> updateTenantStatus(@PathVariable Long id,
                                               @RequestParam String status,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.updateTenantStatus(id, status, operator(userId, username, operator));
        return ResponseVO.success();
    }

    @GetMapping("/orgs/tree")
    public ResponseVO<List<OrgTreeVO>> orgTree(@RequestParam Long tenantId) {
        return ResponseVO.success(service.orgTree(tenantId));
    }

    @PostMapping("/orgs")
    public ResponseVO<OrgTreeVO> createOrg(@Valid @RequestBody OrgRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.createOrg(request, operator(userId, username, operator)));
    }

    @PutMapping("/orgs/{id}")
    public ResponseVO<OrgTreeVO> updateOrg(@PathVariable Long id,
                                           @Valid @RequestBody OrgRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.updateOrg(id, request, operator(userId, username, operator)));
    }

    @DeleteMapping("/orgs/{id}")
    public ResponseVO<Void> deleteOrg(@PathVariable Long id,
                                      @RequestParam Long tenantId,
                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                      @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.deleteOrg(tenantId, id, operator(userId, username, operator));
        return ResponseVO.success();
    }

    @GetMapping("/posts")
    public ResponseVO<PageResult<PostVO>> posts(@RequestParam Long tenantId,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(defaultValue = "1") int pageNo,
                                                @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.pagePosts(tenantId, keyword, pageNo, pageSize));
    }

    @PostMapping("/posts")
    public ResponseVO<PostVO> createPost(@Valid @RequestBody PostRequest request,
                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.createPost(request, operator(userId, username, operator)));
    }

    @PutMapping("/posts/{id}")
    public ResponseVO<PostVO> updatePost(@PathVariable Long id,
                                         @Valid @RequestBody PostRequest request,
                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.updatePost(id, request, operator(userId, username, operator)));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseVO<Void> deletePost(@PathVariable Long id,
                                       @RequestParam Long tenantId,
                                       @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                       @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.deletePost(tenantId, id, operator(userId, username, operator));
        return ResponseVO.success();
    }

    @GetMapping("/users")
    public ResponseVO<PageResult<IamUserVO>> users(@RequestParam Long tenantId,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(defaultValue = "1") int pageNo,
                                                   @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.pageUsers(tenantId, keyword, pageNo, pageSize));
    }

    @PostMapping("/users")
    public ResponseVO<IamUserVO> createUser(@Valid @RequestBody IamUserRequest request,
                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.createUser(request, operator(userId, username, operator)));
    }

    @PutMapping("/users/{id}")
    public ResponseVO<IamUserVO> updateUser(@PathVariable Long id,
                                            @Valid @RequestBody IamUserRequest request,
                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.updateUser(id, request, operator(userId, username, operator)));
    }

    @PutMapping("/users/{id}/status")
    public ResponseVO<Void> updateUserStatus(@PathVariable Long id,
                                             @Valid @RequestBody UserStatusRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.updateUserStatus(id, request, operator(userId, username, operator));
        return ResponseVO.success();
    }

    @PutMapping("/users/{id}/roles")
    public ResponseVO<Void> assignUserRoles(@PathVariable Long id,
                                            @Valid @RequestBody AssignUserRoleRequest request,
                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.assignUserRoles(id, request, operator(userId, username, operator));
        return ResponseVO.success();
    }

    @GetMapping("/users/{id}/permissions")
    public ResponseVO<UserPermissionSummaryVO> userPermissions(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(service.userPermissions(tenantId, id));
    }

    /**
     * 当前登录用户权限摘要；网关透传认证用户 ID 与用户名。
     */
    @GetMapping("/users/me/permissions")
    public ResponseVO<UserPermissionSummaryVO> myPermissions(
            @RequestHeader(value = UserContextHeaders.TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username) {
        Long resolvedTenantId = tenantId == null ? null : Long.valueOf(tenantId);
        Long resolvedAuthUserId = userId == null ? null : Long.valueOf(userId);
        if (resolvedTenantId == null || resolvedTenantId <= 0L) {
            throw new BusinessException(400, "tenantId is required");
        }
        return ResponseVO.success(service.userPermissionsForPrincipal(resolvedTenantId, resolvedAuthUserId, username));
    }

    @GetMapping("/roles")
    public ResponseVO<PageResult<RoleVO>> roles(@RequestParam Long tenantId,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(defaultValue = "1") int pageNo,
                                                @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.pageRoles(tenantId, keyword, pageNo, pageSize));
    }

    @PostMapping("/roles")
    public ResponseVO<RoleVO> createRole(@Valid @RequestBody RoleRequest request,
                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.createRole(request, operator(userId, username, operator)));
    }

    @PutMapping("/roles/{id}")
    public ResponseVO<RoleVO> updateRole(@PathVariable Long id,
                                         @Valid @RequestBody RoleRequest request,
                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.updateRole(id, request, operator(userId, username, operator)));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseVO<Void> deleteRole(@PathVariable Long id,
                                       @RequestParam Long tenantId,
                                       @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                       @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.deleteRole(tenantId, id, operator(userId, username, operator));
        return ResponseVO.success();
    }

    @PutMapping("/roles/{id}/permissions")
    public ResponseVO<Void> assignRolePermissions(@PathVariable Long id,
                                                  @Valid @RequestBody AssignRolePermissionRequest request,
                                                  @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                  @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.assignRolePermissions(id, request, operator(userId, username, operator));
        return ResponseVO.success();
    }

    @PutMapping("/roles/{id}/data-scopes")
    public ResponseVO<Void> assignDataScope(@PathVariable Long id,
                                            @Valid @RequestBody AssignDataScopeRequest request,
                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.assignDataScope(id, request, operator(userId, username, operator));
        return ResponseVO.success();
    }

    @GetMapping("/menus/tree")
    public ResponseVO<List<MenuTreeVO>> menuTree(@RequestParam(defaultValue = "0") Long tenantId) {
        return ResponseVO.success(service.menuTree(tenantId));
    }

    @PostMapping("/menus")
    public ResponseVO<MenuTreeVO> createMenu(@Valid @RequestBody MenuResourceRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.createMenu(request, operator(userId, username, operator)));
    }

    @PutMapping("/menus/{id}")
    public ResponseVO<MenuTreeVO> updateMenu(@PathVariable Long id,
                                             @Valid @RequestBody MenuResourceRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.updateMenu(id, request, operator(userId, username, operator)));
    }

    @DeleteMapping("/menus/{id}")
    public ResponseVO<Void> deleteMenu(@PathVariable Long id,
                                       @RequestParam(defaultValue = "0") Long tenantId,
                                       @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                       @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.deleteMenu(tenantId, id, operator(userId, username, operator));
        return ResponseVO.success();
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
