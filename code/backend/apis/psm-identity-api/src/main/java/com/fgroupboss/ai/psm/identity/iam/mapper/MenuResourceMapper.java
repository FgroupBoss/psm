package com.fgroupboss.ai.psm.identity.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.identity.iam.model.entity.MenuResourceEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单与权限资源持久化操作。
 */
public interface MenuResourceMapper extends BaseMapper<MenuResourceEntity> {

    MenuResourceEntity findById(@Param("tenantId") Long tenantId, @Param("id") Long id);

    MenuResourceEntity findByCode(@Param("tenantId") Long tenantId, @Param("resourceCode") String resourceCode);

    List<MenuResourceEntity> tree(@Param("tenantId") Long tenantId);

    List<MenuResourceEntity> findByUser(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    List<MenuResourceEntity> findByIds(@Param("tenantId") Long tenantId, @Param("ids") List<Long> ids);

    int updateResource(MenuResourceEntity entity);

    int softDelete(@Param("tenantId") Long tenantId, @Param("id") Long id);
}
