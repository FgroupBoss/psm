package com.fgroupboss.ai.psm.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.auth.config.AuthProperties;
import com.fgroupboss.ai.psm.auth.mapper.AuthSessionMapper;
import com.fgroupboss.ai.psm.auth.mapper.AuthSsoStateMapper;
import com.fgroupboss.ai.psm.auth.mapper.AuthUserIdentityMapper;
import com.fgroupboss.ai.psm.auth.mapper.AuthUserMapper;
import com.fgroupboss.ai.psm.auth.mapper.IdentityProviderMapper;
import com.fgroupboss.ai.psm.auth.mapper.LoginLogMapper;
import com.fgroupboss.ai.psm.auth.model.dto.SsoCallbackRequest;
import com.fgroupboss.ai.psm.auth.model.entity.IdentityProviderEntity;
import com.fgroupboss.ai.psm.auth.model.entity.LoginLogEntity;
import com.fgroupboss.ai.psm.auth.model.vo.IdentityProviderVO;
import com.fgroupboss.ai.psm.auth.service.impl.AuthServiceImpl;
import com.fgroupboss.ai.psm.auth.util.PasswordHasher;
import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    private AuthSsoStateMapper authSsoStateMapper;
    private IdentityProviderMapper identityProviderMapper;
    private LoginLogMapper loginLogMapper;
    private AuthServiceImpl service;

    @BeforeEach
    void setUp() {
        authSsoStateMapper = mock(AuthSsoStateMapper.class);
        identityProviderMapper = mock(IdentityProviderMapper.class);
        loginLogMapper = mock(LoginLogMapper.class);
        service = new AuthServiceImpl(
                mock(AuthUserMapper.class),
                mock(AuthSessionMapper.class),
                identityProviderMapper,
                mock(AuthUserIdentityMapper.class),
                authSsoStateMapper,
                loginLogMapper,
                new PasswordHasher(),
                new AuthProperties(),
                new ObjectMapper());
    }

    @Test
    void providersReturnViewObjectsWithoutClientSecret() {
        IdentityProviderEntity entity = provider();
        entity.setClientSecret("secret-value");
        entity.setConfig("{\"issuer\":\"https://idp.example.com\"}");
        when(identityProviderMapper.listEnabled(1L)).thenReturn(Collections.singletonList(entity));

        List<IdentityProviderVO> result = service.providers(1L);

        assertEquals(1, result.size());
        assertEquals("demo-oidc", result.get(0).getProviderCode());
        assertEquals("https://idp.example.com", result.get(0).getConfig().get("issuer"));
        assertFalse(hasMethod(IdentityProviderVO.class, "getClientSecret"));
    }

    @Test
    void ssoCallbackRejectsConsumedStateAndWritesFailureLog() {
        when(identityProviderMapper.findEnabled(1L, "demo-oidc")).thenReturn(provider());
        when(authSsoStateMapper.consume(1L, "demo-oidc", "used-state")).thenReturn(0);
        SsoCallbackRequest request = new SsoCallbackRequest();
        request.setTenantId(1L);
        request.setState("used-state");
        request.setExternalUsername("external-user");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.ssoCallback("demo-oidc", request, "127.0.0.1", "test-agent"));

        assertEquals(401, exception.getCode());
        assertEquals("invalid sso state", exception.getMessage());
        verify(loginLogMapper).insert(any(LoginLogEntity.class));
    }

    private IdentityProviderEntity provider() {
        IdentityProviderEntity entity = new IdentityProviderEntity();
        entity.setTenantId(1L);
        entity.setProviderCode("demo-oidc");
        entity.setProviderType("OIDC");
        entity.setEnabled(Boolean.TRUE);
        return entity;
    }

    private boolean hasMethod(Class<?> type, String methodName) {
        try {
            type.getMethod(methodName);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
