package com.fgroupboss.ai.psm.identity.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.identity.iam.model.entity.UserRoleEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关系持久化操作。
 */
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {

    int deleteByUser(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    List<Long> findRoleIdsByUser(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    List<Long> findUserIdsByRole(@Param("tenantId") Long tenantId, @Param("roleId") Long roleId);
}
