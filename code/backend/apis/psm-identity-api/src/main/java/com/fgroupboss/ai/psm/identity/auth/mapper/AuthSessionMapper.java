package com.fgroupboss.ai.psm.identity.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.identity.auth.model.entity.AuthSessionEntity;
import org.apache.ibatis.annotations.Param;

/**
 * Persistence operations for authentication token sessions.
 */
public interface AuthSessionMapper extends BaseMapper<AuthSessionEntity> {

    AuthSessionEntity findByAccessToken(@Param("accessToken") String accessToken);

    AuthSessionEntity findByRefreshToken(@Param("refreshToken") String refreshToken);

    int revoke(@Param("id") Long id);
}
