package com.fgroupboss.ai.psm.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.auth.model.entity.AuthSsoStateEntity;
import org.apache.ibatis.annotations.Param;

/**
 * Persistence operations for single-use SSO login state values.
 */
public interface AuthSsoStateMapper extends BaseMapper<AuthSsoStateEntity> {

    int consume(@Param("tenantId") Long tenantId,
                @Param("providerCode") String providerCode,
                @Param("state") String state);
}
