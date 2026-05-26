package com.fgroupboss.ai.psm.iam.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.iam.mapper.AuditChangeLogMapper;
import com.fgroupboss.ai.psm.iam.mapper.DataScopeMapper;
import com.fgroupboss.ai.psm.iam.mapper.IamUserMapper;
import com.fgroupboss.ai.psm.iam.mapper.MenuResourceMapper;
import com.fgroupboss.ai.psm.iam.mapper.OrgMapper;
import com.fgroupboss.ai.psm.iam.mapper.PermissionVersionMapper;
import com.fgroupboss.ai.psm.iam.mapper.PostMapper;
import com.fgroupboss.ai.psm.iam.mapper.RoleMapper;
import com.fgroupboss.ai.psm.iam.mapper.RolePermissionMapper;
import com.fgroupboss.ai.psm.iam.mapper.TenantMapper;
import com.fgroupboss.ai.psm.iam.mapper.UserRoleMapper;
import com.fgroupboss.ai.psm.iam.model.dto.AssignUserRoleRequest;
import com.fgroupboss.ai.psm.iam.model.entity.DataScopeEntity;
import com.fgroupboss.ai.psm.iam.model.entity.IamUserEntity;
import com.fgroupboss.ai.psm.iam.model.entity.MenuResourceEntity;
import com.fgroupboss.ai.psm.iam.model.entity.OrgEntity;
import com.fgroupboss.ai.psm.iam.model.entity.PermissionVersionEntity;
import com.fgroupboss.ai.psm.iam.model.entity.RoleEntity;
import com.fgroupboss.ai.psm.iam.model.entity.UserRoleEntity;
import com.fgroupboss.ai.psm.iam.model.vo.OrgTreeVO;
import com.fgroupboss.ai.psm.iam.model.vo.UserPermissionSummaryVO;
import com.fgroupboss.ai.psm.iam.service.impl.IamAdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IamAdminServiceImplTest {

    private OrgMapper orgMapper;
    private IamUserMapper iamUserMapper;
    private RoleMapper roleMapper;
    private UserRoleMapper userRoleMapper;
    private MenuResourceMapper menuResourceMapper;
    private RolePermissionMapper rolePermissionMapper;
    private DataScopeMapper dataScopeMapper;
    private PermissionVersionMapper permissionVersionMapper;
    private IamAdminServiceImpl service;

    @BeforeEach
    void setUp() {
        orgMapper = mock(OrgMapper.class);
        iamUserMapper = mock(IamUserMapper.class);
        roleMapper = mock(RoleMapper.class);
        userRoleMapper = mock(UserRoleMapper.class);
        menuResourceMapper = mock(MenuResourceMapper.class);
        rolePermissionMapper = mock(RolePermissionMapper.class);
        dataScopeMapper = mock(DataScopeMapper.class);
        permissionVersionMapper = mock(PermissionVersionMapper.class);
        service = new IamAdminServiceImpl(
                mock(TenantMapper.class),
                orgMapper,
                mock(PostMapper.class),
                iamUserMapper,
                roleMapper,
                userRoleMapper,
                menuResourceMapper,
                rolePermissionMapper,
                dataScopeMapper,
                permissionVersionMapper,
                mock(AuditChangeLogMapper.class),
                new ObjectMapper());
    }

    @Test
    void orgTreeBuildsParentChildStructure() {
        OrgEntity root = org(1L, null, "site");
        OrgEntity child = org(2L, 1L, "workshop");
        when(orgMapper.tree(10L)).thenReturn(Arrays.asList(root, child));

        List<OrgTreeVO> result = service.orgTree(10L);

        assertEquals(1, result.size());
        assertEquals("site", result.get(0).getOrgName());
        assertEquals(1, result.get(0).getChildren().size());
        assertEquals("workshop", result.get(0).getChildren().get(0).getOrgName());
    }

    @Test
    void userPermissionsDeduplicatesCodesAndReadsDataScopeJson() {
        when(iamUserMapper.findById(10L, 7L)).thenReturn(user());
        PermissionVersionEntity version = new PermissionVersionEntity();
        version.setVersionNo(5L);
        when(permissionVersionMapper.findByUser(10L, 7L)).thenReturn(version);
        when(rolePermissionMapper.findPermissionCodesByUser(10L, 7L))
                .thenReturn(Arrays.asList("permit:create", "permit:create", "alarm:view"));
        when(menuResourceMapper.findByUser(10L, 7L)).thenReturn(Collections.singletonList(menu()));
        DataScopeEntity scope = new DataScopeEntity();
        scope.setRoleId(3L);
        scope.setScopeType("AREA");
        scope.setAreaIds("[11,12]");
        scope.setOrgIds("[]");
        scope.setUnitIds("[]");
        scope.setIncludeChildren(Boolean.TRUE);
        when(dataScopeMapper.findByUser(10L, 7L)).thenReturn(Collections.singletonList(scope));

        UserPermissionSummaryVO result = service.userPermissions(10L, 7L);

        assertEquals(5L, result.getPermissionVersion());
        assertEquals(Arrays.asList("permit:create", "alarm:view"), result.getPermissionCodes());
        assertEquals(1, result.getMenus().size());
        assertEquals(Arrays.asList(11L, 12L), result.getDataScopes().get(0).getAreaIds());
    }

    @Test
    void assignUserRolesReplacesRelationsAndIncreasesPermissionVersion() {
        when(iamUserMapper.findById(10L, 7L)).thenReturn(user());
        when(roleMapper.findById(10L, 3L)).thenReturn(role(3L));
        when(roleMapper.findById(10L, 4L)).thenReturn(role(4L));
        when(iamUserMapper.increasePermissionVersion(10L, 7L)).thenReturn(1);
        when(permissionVersionMapper.increaseUserVersion(10L, 7L, "USER_ROLE_CHANGED")).thenReturn(1);
        AssignUserRoleRequest request = new AssignUserRoleRequest();
        request.setTenantId(10L);
        request.setRoleIds(Arrays.asList(3L, 4L));

        service.assignUserRoles(7L, request, "admin");

        verify(userRoleMapper).deleteByUser(10L, 7L);
        verify(userRoleMapper, times(2)).insert(any(UserRoleEntity.class));
        verify(iamUserMapper).increasePermissionVersion(10L, 7L);
        verify(permissionVersionMapper).increaseUserVersion(10L, 7L, "USER_ROLE_CHANGED");
    }

    private OrgEntity org(Long id, Long parentId, String name) {
        OrgEntity entity = new OrgEntity();
        entity.setId(id);
        entity.setTenantId(10L);
        entity.setParentId(parentId);
        entity.setOrgName(name);
        entity.setOrgCode(name);
        entity.setOrgType("SITE");
        entity.setSortOrder(0);
        entity.setStatus("ENABLED");
        return entity;
    }

    private IamUserEntity user() {
        IamUserEntity entity = new IamUserEntity();
        entity.setId(7L);
        entity.setTenantId(10L);
        entity.setUsername("admin");
        entity.setDisplayName("Admin");
        entity.setStatus("ENABLED");
        entity.setPermissionVersion(1L);
        return entity;
    }

    private RoleEntity role(Long id) {
        RoleEntity entity = new RoleEntity();
        entity.setId(id);
        entity.setTenantId(10L);
        entity.setRoleCode("role-" + id);
        entity.setRoleName("Role " + id);
        entity.setStatus("ENABLED");
        return entity;
    }

    private MenuResourceEntity menu() {
        MenuResourceEntity entity = new MenuResourceEntity();
        entity.setId(1L);
        entity.setTenantId(10L);
        entity.setResourceType("MENU");
        entity.setResourceCode("permit");
        entity.setResourceName("作业许可");
        entity.setVisible(Boolean.TRUE);
        entity.setSortOrder(0);
        entity.setStatus("ENABLED");
        return entity;
    }
}
