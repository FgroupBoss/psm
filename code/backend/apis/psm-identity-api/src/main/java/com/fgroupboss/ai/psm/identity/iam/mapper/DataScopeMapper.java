package com.fgroupboss.ai.psm.identity.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.identity.iam.model.entity.DataScopeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据权限持久化操作。
 */
public interface DataScopeMapper extends BaseMapper<DataScopeEntity> {

    int deleteByRole(@Param("tenantId") Long tenantId, @Param("roleId") Long roleId);

    List<DataScopeEntity> findByUser(@Param("tenantId") Long tenantId, @Param("userId") Long userId);
}
