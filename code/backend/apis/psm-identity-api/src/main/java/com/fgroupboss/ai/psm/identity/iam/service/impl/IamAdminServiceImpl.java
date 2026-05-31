package com.fgroupboss.ai.psm.identity.iam.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.identity.iam.mapper.IamAuditChangeLogMapper;
import com.fgroupboss.ai.psm.identity.iam.mapper.DataScopeMapper;
import com.fgroupboss.ai.psm.identity.iam.mapper.IamUserMapper;
import com.fgroupboss.ai.psm.identity.iam.mapper.MenuResourceMapper;
import com.fgroupboss.ai.psm.identity.iam.mapper.OrgMapper;
import com.fgroupboss.ai.psm.identity.iam.mapper.PermissionVersionMapper;
import com.fgroupboss.ai.psm.identity.iam.mapper.PostMapper;
import com.fgroupboss.ai.psm.identity.iam.mapper.RoleMapper;
import com.fgroupboss.ai.psm.identity.iam.mapper.RolePermissionMapper;
import com.fgroupboss.ai.psm.identity.iam.mapper.TenantMapper;
import com.fgroupboss.ai.psm.identity.iam.mapper.UserRoleMapper;
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
import com.fgroupboss.ai.psm.identity.iam.model.entity.AuditChangeLogEntity;
import com.fgroupboss.ai.psm.identity.iam.model.entity.DataScopeEntity;
import com.fgroupboss.ai.psm.identity.iam.model.entity.IamUserEntity;
import com.fgroupboss.ai.psm.identity.iam.model.entity.MenuResourceEntity;
import com.fgroupboss.ai.psm.identity.iam.model.entity.OrgEntity;
import com.fgroupboss.ai.psm.identity.iam.model.entity.PermissionVersionEntity;
import com.fgroupboss.ai.psm.identity.iam.model.entity.PostEntity;
import com.fgroupboss.ai.psm.identity.iam.model.entity.RoleEntity;
import com.fgroupboss.ai.psm.identity.iam.model.entity.RolePermissionEntity;
import com.fgroupboss.ai.psm.identity.iam.model.entity.TenantEntity;
import com.fgroupboss.ai.psm.identity.iam.model.entity.UserRoleEntity;
import com.fgroupboss.ai.psm.identity.iam.model.vo.DataScopeVO;
import com.fgroupboss.ai.psm.identity.iam.model.vo.IamUserVO;
import com.fgroupboss.ai.psm.identity.iam.model.vo.MenuTreeVO;
import com.fgroupboss.ai.psm.identity.iam.model.vo.OrgTreeVO;
import com.fgroupboss.ai.psm.identity.iam.model.vo.PostVO;
import com.fgroupboss.ai.psm.identity.iam.model.vo.RoleVO;
import com.fgroupboss.ai.psm.identity.iam.model.vo.TenantVO;
import com.fgroupboss.ai.psm.identity.iam.model.vo.UserPermissionSummaryVO;
import com.fgroupboss.ai.psm.identity.iam.service.IamAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * IAM 后台管理服务实现。
 *
 * <p>首期优先保证租户隔离、角色权限和数据范围可被业务服务查询，复杂身份源同步由认证服务承接。</p>
 */
@Service
@RequiredArgsConstructor
public class IamAdminServiceImpl implements IamAdminService {

    private static final TypeReference<List<Long>> LONG_LIST_TYPE = new TypeReference<List<Long>>() {
    };

    private final TenantMapper tenantMapper;
    private final OrgMapper orgMapper;
    private final PostMapper postMapper;
    private final IamUserMapper iamUserMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final MenuResourceMapper menuResourceMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final DataScopeMapper dataScopeMapper;
    private final PermissionVersionMapper permissionVersionMapper;
    private final IamAuditChangeLogMapper auditChangeLogMapper;
    private final ObjectMapper objectMapper;

    /**
     * 实现方式：创建租户，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public TenantVO createTenant(TenantRequest request, String operator) {
        if (tenantMapper.findByCode(request.getTenantCode()) != null) {
            throw new BusinessException(409, "tenantCode already exists: " + request.getTenantCode());
        }
        TenantEntity entity = toTenantEntity(request);
        tenantMapper.insert(entity);
        TenantVO saved = toTenantVO(tenantMapper.findById(entity.getId()));
        writeAudit(saved.getId(), operator, "CREATE", "TENANT", saved.getId(), null, saved);
        return saved;
    }

    /**
     * 实现方式：更新租户，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public TenantVO updateTenant(Long id, TenantRequest request, String operator) {
        TenantVO before = tenant(id);
        TenantEntity entity = toTenantEntity(request);
        entity.setId(id);
        int updated = tenantMapper.updateTenant(entity);
        if (updated == 0) {
            throw new BusinessException(404, "tenant not found");
        }
        TenantVO after = tenant(id);
        writeAudit(id, operator, "UPDATE", "TENANT", id, before, after);
        return after;
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public PageResult<TenantVO> pageTenants(String keyword, int pageNo, int pageSize) {
        Page page = page(pageNo, pageSize);
        String normalizedKeyword = normalizeText(keyword);
        long total = tenantMapper.count(normalizedKeyword);
        List<TenantVO> records = new ArrayList<TenantVO>();
        for (TenantEntity entity : tenantMapper.list(normalizedKeyword, page.limit, page.offset)) {
            records.add(toTenantVO(entity));
        }
        return new PageResult<TenantVO>(total, page.pageNo, page.pageSize, records);
    }

    /**
     * 实现方式：调整租户状态，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void updateTenantStatus(Long id, String status, String operator) {
        TenantVO before = tenant(id);
        if (tenantMapper.updateStatus(id, status) == 0) {
            throw new BusinessException(404, "tenant not found");
        }
        TenantVO after = tenant(id);
        writeAudit(id, operator, "UPDATE_STATUS", "TENANT", id, before, after);
    }

    /**
     * 实现方式：创建组织，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public OrgTreeVO createOrg(OrgRequest request, String operator) {
        validateTenant(request.getTenantId());
        if (orgMapper.findByCode(request.getTenantId(), request.getOrgCode()) != null) {
            throw new BusinessException(409, "orgCode already exists: " + request.getOrgCode());
        }
        OrgEntity entity = toOrgEntity(request);
        orgMapper.insert(entity);
        OrgTreeVO saved = toOrgVO(orgMapper.findById(request.getTenantId(), entity.getId()));
        writeAudit(request.getTenantId(), operator, "CREATE", "ORG", saved.getId(), null, saved);
        return saved;
    }

    /**
     * 实现方式：更新组织，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public OrgTreeVO updateOrg(Long id, OrgRequest request, String operator) {
        OrgTreeVO before = org(request.getTenantId(), id);
        OrgEntity entity = toOrgEntity(request);
        entity.setId(id);
        if (orgMapper.updateOrg(entity) == 0) {
            throw new BusinessException(404, "org not found");
        }
        OrgTreeVO after = org(request.getTenantId(), id);
        writeAudit(request.getTenantId(), operator, "UPDATE", "ORG", id, before, after);
        return after;
    }

    /**
     * 实现方式：删除组织，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void deleteOrg(Long tenantId, Long id, String operator) {
        OrgTreeVO before = org(tenantId, id);
        if (orgMapper.softDelete(tenantId, id) == 0) {
            throw new BusinessException(404, "org not found");
        }
        writeAudit(tenantId, operator, "DELETE", "ORG", id, before, null);
    }

    /**
     * 实现方式：查询组织树，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<OrgTreeVO> orgTree(Long tenantId) {
        validateTenant(tenantId);
        List<OrgTreeVO> nodes = new ArrayList<OrgTreeVO>();
        for (OrgEntity entity : orgMapper.tree(tenantId)) {
            nodes.add(toOrgVO(entity));
        }
        return buildOrgTree(nodes);
    }

    /**
     * 实现方式：创建岗位，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public PostVO createPost(PostRequest request, String operator) {
        validateTenant(request.getTenantId());
        if (postMapper.findByCode(request.getTenantId(), request.getPostCode()) != null) {
            throw new BusinessException(409, "postCode already exists: " + request.getPostCode());
        }
        PostEntity entity = toPostEntity(request);
        postMapper.insert(entity);
        PostVO saved = toPostVO(postMapper.findById(request.getTenantId(), entity.getId()));
        writeAudit(request.getTenantId(), operator, "CREATE", "POST", saved.getId(), null, saved);
        return saved;
    }

    /**
     * 实现方式：更新岗位，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public PostVO updatePost(Long id, PostRequest request, String operator) {
        PostVO before = post(request.getTenantId(), id);
        PostEntity entity = toPostEntity(request);
        entity.setId(id);
        if (postMapper.updatePost(entity) == 0) {
            throw new BusinessException(404, "post not found");
        }
        PostVO after = post(request.getTenantId(), id);
        writeAudit(request.getTenantId(), operator, "UPDATE", "POST", id, before, after);
        return after;
    }

    /**
     * 实现方式：删除岗位，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void deletePost(Long tenantId, Long id, String operator) {
        PostVO before = post(tenantId, id);
        if (postMapper.softDelete(tenantId, id) == 0) {
            throw new BusinessException(404, "post not found");
        }
        writeAudit(tenantId, operator, "DELETE", "POST", id, before, null);
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public PageResult<PostVO> pagePosts(Long tenantId, String keyword, int pageNo, int pageSize) {
        validateTenant(tenantId);
        Page page = page(pageNo, pageSize);
        String normalizedKeyword = normalizeText(keyword);
        long total = postMapper.count(tenantId, normalizedKeyword);
        List<PostVO> records = new ArrayList<PostVO>();
        for (PostEntity entity : postMapper.list(tenantId, normalizedKeyword, page.limit, page.offset)) {
            records.add(toPostVO(entity));
        }
        return new PageResult<PostVO>(total, page.pageNo, page.pageSize, records);
    }

    /**
     * 实现方式：创建用户，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public IamUserVO createUser(IamUserRequest request, String operator) {
        validateTenant(request.getTenantId());
        if (iamUserMapper.findByUsername(request.getTenantId(), request.getUsername()) != null) {
            throw new BusinessException(409, "username already exists: " + request.getUsername());
        }
        IamUserEntity entity = toUserEntity(request);
        iamUserMapper.insert(entity);
        initPermissionVersion(request.getTenantId(), entity.getId(), "USER_CREATED");
        IamUserVO saved = user(request.getTenantId(), entity.getId());
        writeAudit(request.getTenantId(), operator, "CREATE", "USER", saved.getId(), null, saved);
        return saved;
    }

    /**
     * 实现方式：更新用户，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public IamUserVO updateUser(Long id, IamUserRequest request, String operator) {
        IamUserVO before = user(request.getTenantId(), id);
        IamUserEntity entity = toUserEntity(request);
        entity.setId(id);
        if (iamUserMapper.updateUser(entity) == 0) {
            throw new BusinessException(404, "user not found");
        }
        increaseUserVersion(request.getTenantId(), id, "USER_UPDATED");
        IamUserVO after = user(request.getTenantId(), id);
        writeAudit(request.getTenantId(), operator, "UPDATE", "USER", id, before, after);
        return after;
    }

    /**
     * 实现方式：调整用户状态，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void updateUserStatus(Long id, UserStatusRequest request, String operator) {
        IamUserVO before = user(request.getTenantId(), id);
        if (iamUserMapper.updateStatus(request.getTenantId(), id, request.getStatus()) == 0) {
            throw new BusinessException(404, "user not found");
        }
        increaseVersionRecord(request.getTenantId(), id, "USER_STATUS_CHANGED");
        IamUserVO after = user(request.getTenantId(), id);
        writeAudit(request.getTenantId(), operator, "UPDATE_STATUS", "USER", id, before, after);
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public PageResult<IamUserVO> pageUsers(Long tenantId, String keyword, int pageNo, int pageSize) {
        validateTenant(tenantId);
        Page page = page(pageNo, pageSize);
        String normalizedKeyword = normalizeText(keyword);
        long total = iamUserMapper.count(tenantId, normalizedKeyword);
        List<IamUserVO> records = new ArrayList<IamUserVO>();
        for (IamUserEntity entity : iamUserMapper.list(tenantId, normalizedKeyword, page.limit, page.offset)) {
            records.add(toUserVO(entity));
        }
        return new PageResult<IamUserVO>(total, page.pageNo, page.pageSize, records);
    }

    /**
     * 实现方式：分配用户角色，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void assignUserRoles(Long userId, AssignUserRoleRequest request, String operator) {
        IamUserVO before = user(request.getTenantId(), userId);
        userRoleMapper.deleteByUser(request.getTenantId(), userId);
        for (Long roleId : safeIds(request.getRoleIds())) {
            ensureRole(request.getTenantId(), roleId);
            UserRoleEntity relation = new UserRoleEntity();
            relation.setTenantId(request.getTenantId());
            relation.setUserId(userId);
            relation.setRoleId(roleId);
            userRoleMapper.insert(relation);
        }
        increaseUserVersion(request.getTenantId(), userId, "USER_ROLE_CHANGED");
        writeAudit(request.getTenantId(), operator, "ASSIGN_ROLES", "USER", userId, before, request.getRoleIds());
    }

    /**
     * 实现方式：创建角色，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public RoleVO createRole(RoleRequest request, String operator) {
        validateTenant(request.getTenantId());
        if (roleMapper.findByCode(request.getTenantId(), request.getRoleCode()) != null) {
            throw new BusinessException(409, "roleCode already exists: " + request.getRoleCode());
        }
        RoleEntity entity = toRoleEntity(request);
        roleMapper.insert(entity);
        RoleVO saved = role(request.getTenantId(), entity.getId());
        writeAudit(request.getTenantId(), operator, "CREATE", "ROLE", saved.getId(), null, saved);
        return saved;
    }

    /**
     * 实现方式：更新角色，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public RoleVO updateRole(Long id, RoleRequest request, String operator) {
        RoleVO before = role(request.getTenantId(), id);
        RoleEntity entity = toRoleEntity(request);
        entity.setId(id);
        if (roleMapper.updateRole(entity) == 0) {
            throw new BusinessException(404, "role not found");
        }
        touchRoleUsers(request.getTenantId(), id, "ROLE_UPDATED");
        RoleVO after = role(request.getTenantId(), id);
        writeAudit(request.getTenantId(), operator, "UPDATE", "ROLE", id, before, after);
        return after;
    }

    /**
     * 实现方式：删除角色，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void deleteRole(Long tenantId, Long id, String operator) {
        RoleVO before = role(tenantId, id);
        if (roleMapper.softDelete(tenantId, id) == 0) {
            throw new BusinessException(404, "role not found");
        }
        touchRoleUsers(tenantId, id, "ROLE_DELETED");
        writeAudit(tenantId, operator, "DELETE", "ROLE", id, before, null);
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public PageResult<RoleVO> pageRoles(Long tenantId, String keyword, int pageNo, int pageSize) {
        validateTenant(tenantId);
        Page page = page(pageNo, pageSize);
        String normalizedKeyword = normalizeText(keyword);
        long total = roleMapper.count(tenantId, normalizedKeyword);
        List<RoleVO> records = new ArrayList<RoleVO>();
        for (RoleEntity entity : roleMapper.list(tenantId, normalizedKeyword, page.limit, page.offset)) {
            records.add(toRoleVO(entity));
        }
        return new PageResult<RoleVO>(total, page.pageNo, page.pageSize, records);
    }

    /**
     * 实现方式：分配角色权限，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void assignRolePermissions(Long roleId, AssignRolePermissionRequest request, String operator) {
        ensureRole(request.getTenantId(), roleId);
        rolePermissionMapper.deleteByRole(request.getTenantId(), roleId);
        List<MenuResourceEntity> resources = findResources(request.getTenantId(), safeIds(request.getResourceIds()));
        for (MenuResourceEntity resource : resources) {
            RolePermissionEntity relation = new RolePermissionEntity();
            relation.setTenantId(request.getTenantId());
            relation.setRoleId(roleId);
            relation.setResourceId(resource.getId());
            relation.setPermissionCode(resource.getResourceCode());
            rolePermissionMapper.insert(relation);
        }
        touchRoleUsers(request.getTenantId(), roleId, "ROLE_PERMISSION_CHANGED");
        writeAudit(request.getTenantId(), operator, "ASSIGN_PERMISSIONS", "ROLE", roleId, null, request.getResourceIds());
    }

    /**
     * 实现方式：分配角色数据范围，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void assignDataScope(Long roleId, AssignDataScopeRequest request, String operator) {
        ensureRole(request.getTenantId(), roleId);
        dataScopeMapper.deleteByRole(request.getTenantId(), roleId);
        DataScopeEntity entity = new DataScopeEntity();
        entity.setTenantId(request.getTenantId());
        entity.setRoleId(roleId);
        entity.setScopeType(request.getScopeType());
        entity.setOrgIds(toJson(request.getOrgIds()));
        entity.setAreaIds(toJson(request.getAreaIds()));
        entity.setUnitIds(toJson(request.getUnitIds()));
        entity.setIncludeChildren(request.getIncludeChildren() == null ? Boolean.TRUE : request.getIncludeChildren());
        dataScopeMapper.insert(entity);
        touchRoleUsers(request.getTenantId(), roleId, "ROLE_DATA_SCOPE_CHANGED");
        writeAudit(request.getTenantId(), operator, "ASSIGN_DATA_SCOPE", "ROLE", roleId, null, request);
    }

    /**
     * 实现方式：创建菜单资源，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public MenuTreeVO createMenu(MenuResourceRequest request, String operator) {
        Long tenantId = menuTenantId(request.getTenantId());
        if (menuResourceMapper.findByCode(tenantId, request.getResourceCode()) != null) {
            throw new BusinessException(409, "resourceCode already exists: " + request.getResourceCode());
        }
        MenuResourceEntity entity = toMenuEntity(request);
        entity.setTenantId(tenantId);
        menuResourceMapper.insert(entity);
        MenuTreeVO saved = menu(tenantId, entity.getId());
        writeAudit(tenantId, operator, "CREATE", "MENU", saved.getId(), null, saved);
        return saved;
    }

    /**
     * 实现方式：更新菜单资源，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public MenuTreeVO updateMenu(Long id, MenuResourceRequest request, String operator) {
        Long tenantId = menuTenantId(request.getTenantId());
        MenuTreeVO before = menu(tenantId, id);
        MenuResourceEntity entity = toMenuEntity(request);
        entity.setId(id);
        entity.setTenantId(tenantId);
        if (menuResourceMapper.updateResource(entity) == 0) {
            throw new BusinessException(404, "menu resource not found");
        }
        MenuTreeVO after = menu(tenantId, id);
        writeAudit(tenantId, operator, "UPDATE", "MENU", id, before, after);
        return after;
    }

    /**
     * 实现方式：删除菜单资源，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void deleteMenu(Long tenantId, Long id, String operator) {
        Long normalizedTenantId = menuTenantId(tenantId);
        MenuTreeVO before = menu(normalizedTenantId, id);
        if (menuResourceMapper.softDelete(normalizedTenantId, id) == 0) {
            throw new BusinessException(404, "menu resource not found");
        }
        writeAudit(normalizedTenantId, operator, "DELETE", "MENU", id, before, null);
    }

    /**
     * 实现方式：查询菜单树，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<MenuTreeVO> menuTree(Long tenantId) {
        Long normalizedTenantId = menuTenantId(tenantId);
        List<MenuTreeVO> nodes = new ArrayList<MenuTreeVO>();
        for (MenuResourceEntity entity : menuResourceMapper.tree(normalizedTenantId)) {
            nodes.add(toMenuVO(entity));
        }
        return buildMenuTree(nodes);
    }

    /**
     * 实现方式：查询用户权限集合，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public UserPermissionSummaryVO userPermissions(Long tenantId, Long userId) {
        IamUserVO user = user(tenantId, userId);
        UserPermissionSummaryVO summary = new UserPermissionSummaryVO();
        summary.setTenantId(tenantId);
        summary.setUserId(userId);
        summary.setUsername(user.getUsername());
        summary.setPermissionVersion(resolvePermissionVersion(tenantId, userId, user.getPermissionVersion()));
        summary.setPermissionCodes(deduplicate(rolePermissionMapper.findPermissionCodesByUser(tenantId, userId)));
        summary.setMenus(buildMenuTree(toMenuVOList(menuResourceMapper.findByUser(tenantId, userId))));
        summary.setDataScopes(toDataScopeVOList(dataScopeMapper.findByUser(tenantId, userId)));
        return summary;
    }

    /**
     * 实现方式：按登录主体查询权限集合，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public UserPermissionSummaryVO userPermissionsForPrincipal(Long tenantId, Long authUserId, String username) {
        IamUserEntity iamUser = resolveIamUser(tenantId, authUserId, username);
        return userPermissions(tenantId, iamUser.getId());
    }

    private IamUserEntity resolveIamUser(Long tenantId, Long authUserId, String username) {
        validateTenant(tenantId);
        IamUserEntity entity = null;
        if (authUserId != null && authUserId > 0L) {
            entity = iamUserMapper.findByAuthUserId(tenantId, authUserId);
        }
        if (entity == null && org.springframework.util.StringUtils.hasText(username)) {
            entity = iamUserMapper.findByUsername(tenantId, username);
        }
        if (entity == null) {
            throw new BusinessException(404, "iam user not found");
        }
        return entity;
    }

    private TenantVO tenant(Long id) {
        TenantEntity entity = tenantMapper.findById(id);
        if (entity == null) {
            throw new BusinessException(404, "tenant not found");
        }
        return toTenantVO(entity);
    }

    private OrgTreeVO org(Long tenantId, Long id) {
        validateTenant(tenantId);
        OrgEntity entity = orgMapper.findById(tenantId, id);
        if (entity == null) {
            throw new BusinessException(404, "org not found");
        }
        return toOrgVO(entity);
    }

    private PostVO post(Long tenantId, Long id) {
        validateTenant(tenantId);
        PostEntity entity = postMapper.findById(tenantId, id);
        if (entity == null) {
            throw new BusinessException(404, "post not found");
        }
        return toPostVO(entity);
    }

    private IamUserVO user(Long tenantId, Long id) {
        validateTenant(tenantId);
        IamUserEntity entity = iamUserMapper.findById(tenantId, id);
        if (entity == null) {
            throw new BusinessException(404, "user not found");
        }
        return toUserVO(entity);
    }

    private RoleVO role(Long tenantId, Long id) {
        RoleEntity entity = ensureRole(tenantId, id);
        return toRoleVO(entity);
    }

    private RoleEntity ensureRole(Long tenantId, Long id) {
        validateTenant(tenantId);
        RoleEntity entity = roleMapper.findById(tenantId, id);
        if (entity == null) {
            throw new BusinessException(404, "role not found");
        }
        return entity;
    }

    private MenuTreeVO menu(Long tenantId, Long id) {
        MenuResourceEntity entity = menuResourceMapper.findById(tenantId, id);
        if (entity == null) {
            throw new BusinessException(404, "menu resource not found");
        }
        return toMenuVO(entity);
    }

    private List<MenuResourceEntity> findResources(Long tenantId, List<Long> resourceIds) {
        if (CollectionUtils.isEmpty(resourceIds)) {
            return Collections.emptyList();
        }
        List<MenuResourceEntity> resources = menuResourceMapper.findByIds(tenantId, resourceIds);
        if (resources.size() != resourceIds.size()) {
            throw new BusinessException(400, "resourceIds contain invalid resource");
        }
        return resources;
    }

    private void initPermissionVersion(Long tenantId, Long userId, String reason) {
        PermissionVersionEntity entity = new PermissionVersionEntity();
        entity.setTenantId(tenantId);
        entity.setUserId(userId);
        entity.setVersionNo(1L);
        entity.setChangedReason(reason);
        permissionVersionMapper.insert(entity);
    }

    private void increaseUserVersion(Long tenantId, Long userId, String reason) {
        if (iamUserMapper.increasePermissionVersion(tenantId, userId) == 0) {
            throw new BusinessException(404, "user not found");
        }
        increaseVersionRecord(tenantId, userId, reason);
    }

    private void increaseVersionRecord(Long tenantId, Long userId, String reason) {
        if (permissionVersionMapper.increaseUserVersion(tenantId, userId, reason) == 0) {
            initPermissionVersion(tenantId, userId, reason);
        }
    }

    private void touchRoleUsers(Long tenantId, Long roleId, String reason) {
        for (Long userId : userRoleMapper.findUserIdsByRole(tenantId, roleId)) {
            increaseUserVersion(tenantId, userId, reason);
        }
    }

    private Long resolvePermissionVersion(Long tenantId, Long userId, Long fallback) {
        PermissionVersionEntity entity = permissionVersionMapper.findByUser(tenantId, userId);
        return entity == null ? fallback : entity.getVersionNo();
    }

    private TenantEntity toTenantEntity(TenantRequest request) {
        TenantEntity entity = new TenantEntity();
        entity.setTenantCode(request.getTenantCode());
        entity.setTenantName(request.getTenantName());
        entity.setTenantType(defaultText(request.getTenantType(), "PILOT"));
        entity.setStatus(defaultText(request.getStatus(), "ENABLED"));
        entity.setAdminUserId(request.getAdminUserId());
        entity.setDataIsolationMode(defaultText(request.getDataIsolationMode(), "TENANT"));
        return entity;
    }

    private OrgEntity toOrgEntity(OrgRequest request) {
        OrgEntity entity = new OrgEntity();
        entity.setTenantId(request.getTenantId());
        entity.setParentId(request.getParentId());
        entity.setOrgCode(request.getOrgCode());
        entity.setOrgName(request.getOrgName());
        entity.setOrgType(request.getOrgType());
        entity.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        entity.setStatus(defaultText(request.getStatus(), "ENABLED"));
        return entity;
    }

    private PostEntity toPostEntity(PostRequest request) {
        PostEntity entity = new PostEntity();
        entity.setTenantId(request.getTenantId());
        entity.setPostCode(request.getPostCode());
        entity.setPostName(request.getPostName());
        entity.setOrgId(request.getOrgId());
        entity.setStatus(defaultText(request.getStatus(), "ENABLED"));
        return entity;
    }

    private IamUserEntity toUserEntity(IamUserRequest request) {
        IamUserEntity entity = new IamUserEntity();
        entity.setTenantId(request.getTenantId());
        entity.setAuthUserId(request.getAuthUserId());
        entity.setUsername(request.getUsername());
        entity.setDisplayName(request.getDisplayName());
        entity.setMobile(request.getMobile());
        entity.setEmail(request.getEmail());
        entity.setOrgId(request.getOrgId());
        entity.setPostId(request.getPostId());
        entity.setAccountType(defaultText(request.getAccountType(), "LOCAL"));
        entity.setStatus(defaultText(request.getStatus(), "ENABLED"));
        return entity;
    }

    private RoleEntity toRoleEntity(RoleRequest request) {
        RoleEntity entity = new RoleEntity();
        entity.setTenantId(request.getTenantId());
        entity.setRoleCode(request.getRoleCode());
        entity.setRoleName(request.getRoleName());
        entity.setRoleType(defaultText(request.getRoleType(), "BUSINESS"));
        entity.setDescription(request.getDescription());
        entity.setStatus(defaultText(request.getStatus(), "ENABLED"));
        return entity;
    }

    private MenuResourceEntity toMenuEntity(MenuResourceRequest request) {
        MenuResourceEntity entity = new MenuResourceEntity();
        entity.setParentId(request.getParentId());
        entity.setResourceType(request.getResourceType());
        entity.setResourceCode(request.getResourceCode());
        entity.setResourceName(request.getResourceName());
        entity.setRoutePath(request.getRoutePath());
        entity.setApiPath(request.getApiPath());
        entity.setHttpMethod(request.getHttpMethod());
        entity.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        entity.setVisible(request.getVisible());
        entity.setStatus(defaultText(request.getStatus(), "ENABLED"));
        return entity;
    }

    private TenantVO toTenantVO(TenantEntity entity) {
        TenantVO vo = new TenantVO();
        vo.setId(entity.getId());
        vo.setTenantCode(entity.getTenantCode());
        vo.setTenantName(entity.getTenantName());
        vo.setTenantType(entity.getTenantType());
        vo.setStatus(entity.getStatus());
        vo.setAdminUserId(entity.getAdminUserId());
        vo.setDataIsolationMode(entity.getDataIsolationMode());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private OrgTreeVO toOrgVO(OrgEntity entity) {
        OrgTreeVO vo = new OrgTreeVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setParentId(entity.getParentId());
        vo.setOrgCode(entity.getOrgCode());
        vo.setOrgName(entity.getOrgName());
        vo.setOrgType(entity.getOrgType());
        vo.setSortOrder(entity.getSortOrder());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    private PostVO toPostVO(PostEntity entity) {
        PostVO vo = new PostVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setPostCode(entity.getPostCode());
        vo.setPostName(entity.getPostName());
        vo.setOrgId(entity.getOrgId());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    private IamUserVO toUserVO(IamUserEntity entity) {
        IamUserVO vo = new IamUserVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setAuthUserId(entity.getAuthUserId());
        vo.setUsername(entity.getUsername());
        vo.setDisplayName(entity.getDisplayName());
        vo.setMobile(entity.getMobile());
        vo.setEmail(entity.getEmail());
        vo.setOrgId(entity.getOrgId());
        vo.setPostId(entity.getPostId());
        vo.setAccountType(entity.getAccountType());
        vo.setStatus(entity.getStatus());
        vo.setPermissionVersion(entity.getPermissionVersion());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private RoleVO toRoleVO(RoleEntity entity) {
        RoleVO vo = new RoleVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setRoleCode(entity.getRoleCode());
        vo.setRoleName(entity.getRoleName());
        vo.setRoleType(entity.getRoleType());
        vo.setDescription(entity.getDescription());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    private MenuTreeVO toMenuVO(MenuResourceEntity entity) {
        MenuTreeVO vo = new MenuTreeVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setParentId(entity.getParentId());
        vo.setResourceType(entity.getResourceType());
        vo.setResourceCode(entity.getResourceCode());
        vo.setResourceName(entity.getResourceName());
        vo.setRoutePath(entity.getRoutePath());
        vo.setApiPath(entity.getApiPath());
        vo.setHttpMethod(entity.getHttpMethod());
        vo.setSortOrder(entity.getSortOrder());
        vo.setVisible(entity.getVisible());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    private List<MenuTreeVO> toMenuVOList(List<MenuResourceEntity> entities) {
        List<MenuTreeVO> records = new ArrayList<MenuTreeVO>();
        for (MenuResourceEntity entity : entities) {
            records.add(toMenuVO(entity));
        }
        return records;
    }

    private List<DataScopeVO> toDataScopeVOList(List<DataScopeEntity> entities) {
        List<DataScopeVO> records = new ArrayList<DataScopeVO>();
        for (DataScopeEntity entity : entities) {
            DataScopeVO vo = new DataScopeVO();
            vo.setRoleId(entity.getRoleId());
            vo.setScopeType(entity.getScopeType());
            vo.setOrgIds(fromJsonIds(entity.getOrgIds()));
            vo.setAreaIds(fromJsonIds(entity.getAreaIds()));
            vo.setUnitIds(fromJsonIds(entity.getUnitIds()));
            vo.setIncludeChildren(entity.getIncludeChildren());
            records.add(vo);
        }
        return records;
    }

    private List<OrgTreeVO> buildOrgTree(List<OrgTreeVO> nodes) {
        Map<Long, OrgTreeVO> byId = new HashMap<Long, OrgTreeVO>();
        List<OrgTreeVO> roots = new ArrayList<OrgTreeVO>();
        for (OrgTreeVO node : nodes) {
            byId.put(node.getId(), node);
        }
        for (OrgTreeVO node : nodes) {
            OrgTreeVO parent = node.getParentId() == null ? null : byId.get(node.getParentId());
            if (parent == null) {
                roots.add(node);
            } else {
                parent.getChildren().add(node);
            }
        }
        return roots;
    }

    private List<MenuTreeVO> buildMenuTree(List<MenuTreeVO> nodes) {
        Map<Long, MenuTreeVO> byId = new HashMap<Long, MenuTreeVO>();
        List<MenuTreeVO> roots = new ArrayList<MenuTreeVO>();
        for (MenuTreeVO node : nodes) {
            byId.put(node.getId(), node);
        }
        for (MenuTreeVO node : nodes) {
            MenuTreeVO parent = node.getParentId() == null ? null : byId.get(node.getParentId());
            if (parent == null) {
                roots.add(node);
            } else {
                parent.getChildren().add(node);
            }
        }
        return roots;
    }

    private void validateTenant(Long tenantId) {
        if (tenantId == null || tenantId.longValue() <= 0L) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private Long menuTenantId(Long tenantId) {
        return tenantId == null ? 0L : tenantId;
    }

    private Page page(int pageNo, int pageSize) {
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);
        return new Page(normalizedPageNo, normalizedPageSize);
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String defaultText(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    private List<Long> safeIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return new ArrayList<Long>(new LinkedHashSet<Long>(ids));
    }

    private List<String> deduplicate(List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        return new ArrayList<String>(new LinkedHashSet<String>(values));
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value == null ? Collections.emptyList() : value);
        } catch (Exception e) {
            throw new BusinessException(400, "data scope ids must be JSON serializable");
        }
    }

    private String toJsonOrNull(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "{}";
        }
    }

    private List<Long> fromJsonIds(String value) {
        if (!StringUtils.hasText(value)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(value, LONG_LIST_TYPE);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private void writeAudit(Long tenantId, String operator, String action, String bizType, Long bizId,
                            Object beforeValue, Object afterValue) {
        AuditChangeLogEntity entity = new AuditChangeLogEntity();
        entity.setTenantId(tenantId);
        entity.setOperatorName(defaultText(operator, "system"));
        entity.setAction(action);
        entity.setBizType(bizType);
        entity.setBizId(bizId);
        entity.setBeforeValue(toJsonOrNull(beforeValue));
        entity.setAfterValue(toJsonOrNull(afterValue));
        entity.setResult("SUCCESS");
        entity.setOperatedAt(LocalDateTime.now());
        auditChangeLogMapper.insert(entity);
    }

    private static class Page {
        private final int pageNo;
        private final int pageSize;
        private final int limit;
        private final int offset;

        private Page(int pageNo, int pageSize) {
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.limit = pageSize;
            this.offset = (pageNo - 1) * pageSize;
        }
    }
}
