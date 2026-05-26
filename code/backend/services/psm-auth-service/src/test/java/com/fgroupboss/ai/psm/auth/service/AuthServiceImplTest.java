package com.fgroupboss.ai.psm.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.auth.client.IamPermissionClient;
import com.fgroupboss.ai.psm.auth.config.AuthProperties;
import com.fgroupboss.ai.psm.auth.mapper.AuthSessionMapper;
import com.fgroupboss.ai.psm.auth.mapper.AuthSsoStateMapper;
import com.fgroupboss.ai.psm.auth.mapper.AuthUserIdentityMapper;
import com.fgroupboss.ai.psm.auth.mapper.AuthUserMapper;
import com.fgroupboss.ai.psm.auth.mapper.IdentityProviderMapper;
import com.fgroupboss.ai.psm.auth.mapper.LoginLogMapper;
import com.fgroupboss.ai.psm.auth.model.dto.SsoCallbackRequest;
import com.fgroupboss.ai.psm.auth.model.entity.AuthSessionEntity;
import com.fgroupboss.ai.psm.auth.model.entity.AuthUserEntity;
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
    private AuthSessionMapper authSessionMapper;
    private AuthUserMapper authUserMapper;
    private IdentityProviderMapper identityProviderMapper;
    private LoginLogMapper loginLogMapper;
    private IamPermissionClient iamPermissionClient;
    private AuthServiceImpl service;

    @BeforeEach
    void setUp() {
        authUserMapper = mock(AuthUserMapper.class);
        authSessionMapper = mock(AuthSessionMapper.class);
        authSsoStateMapper = mock(AuthSsoStateMapper.class);
        identityProviderMapper = mock(IdentityProviderMapper.class);
        loginLogMapper = mock(LoginLogMapper.class);
        iamPermissionClient = mock(IamPermissionClient.class);
        service = new AuthServiceImpl(
                authUserMapper,
                authSessionMapper,
                identityProviderMapper,
                mock(AuthUserIdentityMapper.class),
                authSsoStateMapper,
                loginLogMapper,
                new PasswordHasher(),
                new AuthProperties(),
                new ObjectMapper(),
                iamPermissionClient);
        when(iamPermissionClient.currentPermissionVersion(1L, null)).thenReturn(1L);
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

    @Test
    void meRejectsTokenWhenPermissionVersionExpired() {
        AuthSessionEntity session = new AuthSessionEntity();
        session.setId(99L);
        session.setTenantId(1L);
        session.setUserId(7L);
        session.setPermissionVersion(1L);
        session.setAccessExpiresAt(java.time.LocalDateTime.now().plusMinutes(5));
        when(authSessionMapper.findByAccessToken("token-value")).thenReturn(session);
        when(iamPermissionClient.currentPermissionVersion(1L, 7L)).thenReturn(2L);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.me("Bearer token-value"));

        assertEquals(401, exception.getCode());
        assertEquals("permission version expired", exception.getMessage());
        verify(authSessionMapper).revoke(99L);
    }

    @Test
    void meReturnsCurrentPermissionVersionWhenTokenIsFresh() {
        AuthSessionEntity session = new AuthSessionEntity();
        session.setTenantId(1L);
        session.setUserId(7L);
        session.setPermissionVersion(3L);
        session.setAccessExpiresAt(java.time.LocalDateTime.now().plusMinutes(5));
        when(authSessionMapper.findByAccessToken("token-value")).thenReturn(session);
        when(iamPermissionClient.currentPermissionVersion(1L, 7L)).thenReturn(3L);
        AuthUserEntity user = new AuthUserEntity();
        user.setId(7L);
        user.setTenantId(1L);
        user.setUsername("admin");
        user.setDisplayName("Admin");
        user.setAccountType("LOCAL");
        user.setStatus("ENABLED");
        when(authUserMapper.findByIdAndTenant(1L, 7L)).thenReturn(user);

        assertEquals(3L, service.me("Bearer token-value").getPermissionVersion());
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
