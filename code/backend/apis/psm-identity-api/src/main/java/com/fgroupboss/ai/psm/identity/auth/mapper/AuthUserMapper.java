package com.fgroupboss.ai.psm.identity.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.identity.auth.model.entity.AuthUserEntity;
import org.apache.ibatis.annotations.Param;

/**
 * Persistence operations for tenant authentication users.
 */
public interface AuthUserMapper extends BaseMapper<AuthUserEntity> {

    AuthUserEntity findByUsername(@Param("tenantId") Long tenantId, @Param("username") String username);

    AuthUserEntity findByIdAndTenant(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    AuthUserEntity findByIdentity(@Param("tenantId") Long tenantId,
                                  @Param("providerCode") String providerCode,
                                  @Param("externalUserId") String externalUserId);

    int updateLastLogin(@Param("tenantId") Long tenantId, @Param("userId") Long userId);
}
