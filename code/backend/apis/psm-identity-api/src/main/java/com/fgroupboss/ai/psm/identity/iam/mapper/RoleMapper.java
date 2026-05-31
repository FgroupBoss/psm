package com.fgroupboss.ai.psm.identity.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.identity.iam.model.entity.RoleEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色持久化操作。
 */
public interface RoleMapper extends BaseMapper<RoleEntity> {

    RoleEntity findById(@Param("tenantId") Long tenantId, @Param("id") Long id);

    RoleEntity findByCode(@Param("tenantId") Long tenantId, @Param("roleCode") String roleCode);

    List<RoleEntity> list(@Param("tenantId") Long tenantId,
                          @Param("keyword") String keyword,
                          @Param("limit") int limit,
                          @Param("offset") int offset);

    long count(@Param("tenantId") Long tenantId, @Param("keyword") String keyword);

    int updateRole(RoleEntity entity);

    int softDelete(@Param("tenantId") Long tenantId, @Param("id") Long id);
}
