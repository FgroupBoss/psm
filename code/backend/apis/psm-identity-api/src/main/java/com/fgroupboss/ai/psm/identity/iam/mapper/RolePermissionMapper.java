package com.fgroupboss.ai.psm.identity.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.identity.iam.model.entity.RolePermissionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色权限关系持久化操作。
 */
public interface RolePermissionMapper extends BaseMapper<RolePermissionEntity> {

    int deleteByRole(@Param("tenantId") Long tenantId, @Param("roleId") Long roleId);

    List<String> findPermissionCodesByUser(@Param("tenantId") Long tenantId, @Param("userId") Long userId);
}
