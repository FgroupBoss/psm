package com.fgroupboss.ai.psm.identity.iam.service;

import com.fgroupboss.ai.psm.common.PageResult;
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

import java.util.List;

/**
 * IAM 后台管理服务。
 */
public interface IamAdminService {

    TenantVO createTenant(TenantRequest request, String operator);

    TenantVO updateTenant(Long id, TenantRequest request, String operator);

    PageResult<TenantVO> pageTenants(String keyword, int pageNo, int pageSize);

    void updateTenantStatus(Long id, String status, String operator);

    OrgTreeVO createOrg(OrgRequest request, String operator);

    OrgTreeVO updateOrg(Long id, OrgRequest request, String operator);

    void deleteOrg(Long tenantId, Long id, String operator);

    List<OrgTreeVO> orgTree(Long tenantId);

    PostVO createPost(PostRequest request, String operator);

    PostVO updatePost(Long id, PostRequest request, String operator);

    void deletePost(Long tenantId, Long id, String operator);

    PageResult<PostVO> pagePosts(Long tenantId, String keyword, int pageNo, int pageSize);

    IamUserVO createUser(IamUserRequest request, String operator);

    IamUserVO updateUser(Long id, IamUserRequest request, String operator);

    void updateUserStatus(Long id, UserStatusRequest request, String operator);

    PageResult<IamUserVO> pageUsers(Long tenantId, String keyword, int pageNo, int pageSize);

    void assignUserRoles(Long userId, AssignUserRoleRequest request, String operator);

    RoleVO createRole(RoleRequest request, String operator);

    RoleVO updateRole(Long id, RoleRequest request, String operator);

    void deleteRole(Long tenantId, Long id, String operator);

    PageResult<RoleVO> pageRoles(Long tenantId, String keyword, int pageNo, int pageSize);

    void assignRolePermissions(Long roleId, AssignRolePermissionRequest request, String operator);

    void assignDataScope(Long roleId, AssignDataScopeRequest request, String operator);

    MenuTreeVO createMenu(MenuResourceRequest request, String operator);

    MenuTreeVO updateMenu(Long id, MenuResourceRequest request, String operator);

    void deleteMenu(Long tenantId, Long id, String operator);

    List<MenuTreeVO> menuTree(Long tenantId);

    UserPermissionSummaryVO userPermissions(Long tenantId, Long userId);

    /**
     * 按认证用户身份解析 IAM 用户并返回权限摘要，供网关透传后的管理端拉取菜单。
     */
    UserPermissionSummaryVO userPermissionsForPrincipal(Long tenantId, Long authUserId, String username);
}
