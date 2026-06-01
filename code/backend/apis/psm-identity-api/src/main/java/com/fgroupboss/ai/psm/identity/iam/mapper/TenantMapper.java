package com.fgroupboss.ai.psm.identity.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.identity.iam.model.entity.TenantEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租户持久化操作。
 */
public interface TenantMapper extends BaseMapper<TenantEntity> {

    TenantEntity findById(@Param("id") Long id);

    TenantEntity findByCode(@Param("tenantCode") String tenantCode);

    List<TenantEntity> list(@Param("keyword") String keyword, @Param("limit") int limit, @Param("offset") int offset);

    long count(@Param("keyword") String keyword);

    int updateTenant(TenantEntity entity);

    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
