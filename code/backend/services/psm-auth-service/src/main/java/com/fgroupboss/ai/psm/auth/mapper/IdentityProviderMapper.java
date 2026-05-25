package com.fgroupboss.ai.psm.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fgroupboss.ai.psm.auth.model.entity.IdentityProviderEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Persistence operations for configured SSO identity providers.
 */
public interface IdentityProviderMapper extends BaseMapper<IdentityProviderEntity> {

    List<IdentityProviderEntity> listEnabled(@Param("tenantId") Long tenantId);

    IdentityProviderEntity findEnabled(@Param("tenantId") Long tenantId,
                                       @Param("providerCode") String providerCode);
}
