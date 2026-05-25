package com.fgroupboss.ai.psm.auth.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.auth.config.AuthProperties;
import com.fgroupboss.ai.psm.auth.mapper.AuthSessionMapper;
import com.fgroupboss.ai.psm.auth.mapper.AuthSsoStateMapper;
import com.fgroupboss.ai.psm.auth.mapper.AuthUserIdentityMapper;
import com.fgroupboss.ai.psm.auth.mapper.AuthUserMapper;
import com.fgroupboss.ai.psm.auth.mapper.IdentityProviderMapper;
import com.fgroupboss.ai.psm.auth.mapper.LoginLogMapper;
import com.fgroupboss.ai.psm.auth.model.dto.LoginRequest;
import com.fgroupboss.ai.psm.auth.model.dto.RefreshTokenRequest;
import com.fgroupboss.ai.psm.auth.model.dto.RegisterRequest;
import com.fgroupboss.ai.psm.auth.model.dto.SsoCallbackRequest;
import com.fgroupboss.ai.psm.auth.model.entity.AuthSessionEntity;
import com.fgroupboss.ai.psm.auth.model.entity.AuthSsoStateEntity;
import com.fgroupboss.ai.psm.auth.model.entity.AuthUserEntity;
import com.fgroupboss.ai.psm.auth.model.entity.AuthUserIdentityEntity;
import com.fgroupboss.ai.psm.auth.model.entity.IdentityProviderEntity;
import com.fgroupboss.ai.psm.auth.model.entity.LoginLogEntity;
import com.fgroupboss.ai.psm.auth.model.vo.AuthTokenResponse;
import com.fgroupboss.ai.psm.auth.model.vo.AuthUserVO;
import com.fgroupboss.ai.psm.auth.model.vo.IdentityProviderVO;
import com.fgroupboss.ai.psm.auth.model.vo.SsoLoginResponse;
import com.fgroupboss.ai.psm.auth.service.AuthService;
import com.fgroupboss.ai.psm.auth.util.PasswordHasher;
import com.fgroupboss.ai.psm.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Implements authentication workflows using MyBatis-Plus mappers.
 *
 * <p>Methods that mutate sessions, one-time SSO state, or login audit records
 * retain a single transaction boundary to preserve the original workflow semantics.</p>
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {
    };

    private final AuthUserMapper authUserMapper;
    private final AuthSessionMapper authSessionMapper;
    private final IdentityProviderMapper identityProviderMapper;
    private final AuthUserIdentityMapper authUserIdentityMapper;
    private final AuthSsoStateMapper authSsoStateMapper;
    private final LoginLogMapper loginLogMapper;
    private final PasswordHasher passwordHasher;
    private final AuthProperties properties;
    private final ObjectMapper objectMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public AuthUserVO register(RegisterRequest request) {
        validateTenant(request.getTenantId());
        if (authUserMapper.findByUsername(request.getTenantId(), request.getUsername()) != null) {
            throw new BusinessException(409, "username already exists");
        }
        AuthUserEntity user = createLocalUser(request);
        authUserMapper.insert(user);
        return toUserVO(authUserMapper.findByIdAndTenant(user.getTenantId(), user.getId()));
    }

    @Override
    @Transactional
    public AuthTokenResponse login(LoginRequest request, String clientIp, String userAgent) {
        validateTenant(request.getTenantId());
        AuthUserEntity user = authUserMapper.findByUsername(request.getTenantId(), request.getUsername());
        if (user == null || !passwordHasher.matches(request.getPassword(), user.getPasswordHash())) {
            insertLoginLog(request.getTenantId(), null, request.getUsername(), "LOCAL", "FAILURE",
                    "bad credentials", clientIp, userAgent);
            throw new BusinessException(401, "bad credentials");
        }
        ensureEnabled(user);
        authUserMapper.updateLastLogin(user.getTenantId(), user.getId());
        insertLoginLog(user.getTenantId(), user.getId(), user.getUsername(), "LOCAL", "SUCCESS",
                null, clientIp, userAgent);
        return issueToken(user);
    }

    @Override
    @Transactional
    public AuthTokenResponse refresh(RefreshTokenRequest request) {
        AuthSessionEntity session = authSessionMapper.findByRefreshToken(request.getRefreshToken());
        if (session == null || session.getRefreshExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(401, "refresh token expired");
        }
        AuthUserEntity user = requireUser(session.getTenantId(), session.getUserId());
        ensureEnabled(user);
        authSessionMapper.revoke(session.getId());
        return issueToken(user);
    }

    @Override
    @Transactional
    public void logout(String accessToken) {
        authSessionMapper.revoke(currentSession(accessToken).getId());
    }

    @Override
    public AuthUserVO me(String accessToken) {
        AuthSessionEntity session = currentSession(accessToken);
        AuthUserEntity user = requireUser(session.getTenantId(), session.getUserId());
        ensureEnabled(user);
        return toUserVO(user);
    }

    @Override
    public List<IdentityProviderVO> providers(Long tenantId) {
        validateTenant(tenantId);
        List<IdentityProviderVO> result = new ArrayList<IdentityProviderVO>();
        for (IdentityProviderEntity provider : identityProviderMapper.listEnabled(tenantId)) {
            result.add(toProviderVO(provider));
        }
        return result;
    }

    @Override
    @Transactional
    public SsoLoginResponse startSso(Long tenantId, String providerCode, String redirectAfterLogin) {
        validateTenant(tenantId);
        IdentityProviderEntity provider = requireProvider(tenantId, providerCode);
        String state = randomToken();
        insertSsoState(tenantId, providerCode, state, redirectAfterLogin);
        SsoLoginResponse response = new SsoLoginResponse();
        response.setProviderCode(providerCode);
        response.setProviderType(provider.getProviderType());
        response.setState(state);
        response.setRedirectUrl(buildRedirectUrl(provider, state));
        return response;
    }

    @Override
    @Transactional
    public AuthTokenResponse ssoCallback(String providerCode, SsoCallbackRequest request,
                                         String clientIp, String userAgent) {
        validateTenant(request.getTenantId());
        IdentityProviderEntity provider = requireProvider(request.getTenantId(), providerCode);
        if (authSsoStateMapper.consume(request.getTenantId(), providerCode, request.getState()) <= 0) {
            insertLoginLog(request.getTenantId(), null, request.getExternalUsername(), provider.getProviderType(),
                    "FAILURE", "invalid sso state", clientIp, userAgent);
            throw new BusinessException(401, "invalid sso state");
        }
        AuthUserEntity user = resolveSsoUser(provider, request);
        ensureEnabled(user);
        authUserMapper.updateLastLogin(user.getTenantId(), user.getId());
        insertLoginLog(user.getTenantId(), user.getId(), user.getUsername(), provider.getProviderType(),
                "SUCCESS", null, clientIp, userAgent);
        return issueToken(user);
    }

    private AuthUserEntity createLocalUser(RegisterRequest request) {
        AuthUserEntity user = new AuthUserEntity();
        user.setTenantId(request.getTenantId());
        user.setUsername(request.getUsername());
        user.setDisplayName(request.getDisplayName());
        user.setMobile(request.getMobile());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordHasher.hash(request.getPassword()));
        user.setAccountType("LOCAL");
        user.setStatus("ENABLED");
        return user;
    }

    private AuthUserEntity resolveSsoUser(IdentityProviderEntity provider, SsoCallbackRequest request) {
        String externalUserId = resolveExternalUserId(request, provider);
        AuthUserEntity user = authUserMapper.findByIdentity(request.getTenantId(),
                provider.getProviderCode(), externalUserId);
        if (user != null) {
            return user;
        }
        user = createSsoUser(provider, request, externalUserId);
        insertUserIdentity(user, provider.getProviderCode(), externalUserId, request.getExternalUsername());
        return user;
    }

    private AuthUserEntity createSsoUser(IdentityProviderEntity provider, SsoCallbackRequest request,
                                         String externalUserId) {
        AuthUserEntity user = new AuthUserEntity();
        user.setTenantId(request.getTenantId());
        user.setUsername(provider.getProviderCode() + "_" + externalUserId);
        user.setDisplayName(StringUtils.hasText(request.getDisplayName())
                ? request.getDisplayName() : request.getExternalUsername());
        user.setAccountType("SSO");
        user.setStatus("ENABLED");
        authUserMapper.insert(user);
        return authUserMapper.findByIdAndTenant(request.getTenantId(), user.getId());
    }

    private void insertUserIdentity(AuthUserEntity user, String providerCode, String externalUserId,
                                    String externalUsername) {
        AuthUserIdentityEntity identity = new AuthUserIdentityEntity();
        identity.setTenantId(user.getTenantId());
        identity.setUserId(user.getId());
        identity.setProviderCode(providerCode);
        identity.setExternalUserId(externalUserId);
        identity.setExternalUsername(externalUsername);
        identity.setEnabled(Boolean.TRUE);
        authUserIdentityMapper.insert(identity);
    }

    private AuthTokenResponse issueToken(AuthUserEntity user) {
        LocalDateTime now = LocalDateTime.now();
        AuthSessionEntity session = new AuthSessionEntity();
        session.setTenantId(user.getTenantId());
        session.setUserId(user.getId());
        session.setAccessToken(randomToken());
        session.setRefreshToken(randomToken());
        session.setAccessExpiresAt(now.plusSeconds(properties.getTokenTtlSeconds()));
        session.setRefreshExpiresAt(now.plusSeconds(properties.getRefreshTokenTtlSeconds()));
        session.setRevoked(Boolean.FALSE);
        authSessionMapper.insert(session);
        return toTokenResponse(session, user);
    }

    private AuthTokenResponse toTokenResponse(AuthSessionEntity session, AuthUserEntity user) {
        AuthTokenResponse response = new AuthTokenResponse();
        response.setAccessToken(session.getAccessToken());
        response.setTokenType("Bearer");
        response.setRefreshToken(session.getRefreshToken());
        response.setAccessExpiresAt(session.getAccessExpiresAt());
        response.setRefreshExpiresAt(session.getRefreshExpiresAt());
        response.setUser(toUserVO(user));
        return response;
    }

    private AuthSessionEntity currentSession(String accessToken) {
        String token = normalizeBearer(accessToken);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(401, "access token is required");
        }
        AuthSessionEntity session = authSessionMapper.findByAccessToken(token);
        if (session == null || session.getAccessExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(401, "access token expired");
        }
        return session;
    }

    private AuthUserEntity requireUser(Long tenantId, Long userId) {
        AuthUserEntity user = authUserMapper.findByIdAndTenant(tenantId, userId);
        if (user == null) {
            throw new BusinessException(401, "user not found");
        }
        return user;
    }

    private IdentityProviderEntity requireProvider(Long tenantId, String providerCode) {
        if (!StringUtils.hasText(providerCode)) {
            throw new BusinessException(400, "providerCode is required");
        }
        IdentityProviderEntity provider = identityProviderMapper.findEnabled(tenantId, providerCode);
        if (provider == null) {
            throw new BusinessException(404, "identity provider not found");
        }
        return provider;
    }

    private String resolveExternalUserId(SsoCallbackRequest request, IdentityProviderEntity provider) {
        if (StringUtils.hasText(request.getExternalUserId())) {
            return request.getExternalUserId();
        }
        if (request.getClaims() != null && StringUtils.hasText(provider.getUserMappingField())) {
            Object value = request.getClaims().get(provider.getUserMappingField());
            if (value != null && StringUtils.hasText(String.valueOf(value))) {
                return String.valueOf(value);
            }
        }
        if (StringUtils.hasText(request.getExternalUsername())) {
            return request.getExternalUsername();
        }
        throw new BusinessException(400, "external user id is required");
    }

    private void insertSsoState(Long tenantId, String providerCode, String state, String redirectAfterLogin) {
        AuthSsoStateEntity entity = new AuthSsoStateEntity();
        entity.setTenantId(tenantId);
        entity.setProviderCode(providerCode);
        entity.setState(state);
        entity.setExpiresAt(LocalDateTime.now().plusSeconds(properties.getSsoStateTtlSeconds()));
        entity.setRedirectAfterLogin(redirectAfterLogin);
        entity.setConsumed(Boolean.FALSE);
        authSsoStateMapper.insert(entity);
    }

    private void insertLoginLog(Long tenantId, Long userId, String username, String loginType, String result,
                                String reason, String clientIp, String userAgent) {
        LoginLogEntity log = new LoginLogEntity();
        log.setTenantId(tenantId);
        log.setUserId(userId);
        log.setUsername(username);
        log.setLoginType(loginType);
        log.setResult(result);
        log.setFailureReason(reason);
        log.setClientIp(clientIp);
        log.setUserAgent(userAgent);
        loginLogMapper.insert(log);
    }

    private AuthUserVO toUserVO(AuthUserEntity user) {
        AuthUserVO response = new AuthUserVO();
        response.setId(user.getId());
        response.setTenantId(user.getTenantId());
        response.setUsername(user.getUsername());
        response.setDisplayName(user.getDisplayName());
        response.setMobile(user.getMobile());
        response.setEmail(user.getEmail());
        response.setAccountType(user.getAccountType());
        response.setStatus(user.getStatus());
        response.setLastLoginAt(user.getLastLoginAt());
        return response;
    }

    private IdentityProviderVO toProviderVO(IdentityProviderEntity provider) {
        IdentityProviderVO response = new IdentityProviderVO();
        response.setId(provider.getId());
        response.setTenantId(provider.getTenantId());
        response.setProviderCode(provider.getProviderCode());
        response.setProviderType(provider.getProviderType());
        response.setProviderName(provider.getProviderName());
        response.setClientId(provider.getClientId());
        response.setAuthorizeUrl(provider.getAuthorizeUrl());
        response.setTokenUrl(provider.getTokenUrl());
        response.setUserInfoUrl(provider.getUserInfoUrl());
        response.setCallbackUrl(provider.getCallbackUrl());
        response.setUserMappingField(provider.getUserMappingField());
        response.setConfig(parseConfig(provider.getConfig()));
        response.setEnabled(Boolean.TRUE.equals(provider.getEnabled()));
        return response;
    }

    private Map<String, Object> parseConfig(String config) {
        try {
            return StringUtils.hasText(config) ? objectMapper.readValue(config, MAP_TYPE) : Collections.emptyMap();
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private String buildRedirectUrl(IdentityProviderEntity provider, String state) {
        String callback = StringUtils.hasText(provider.getCallbackUrl())
                ? provider.getCallbackUrl()
                : properties.getPublicBaseUrl() + "/auth/sso/" + provider.getProviderCode() + "/callback";
        String type = provider.getProviderType() == null ? "" : provider.getProviderType().toUpperCase(Locale.ROOT);
        if ("OIDC".equals(type) || "OAUTH2".equals(type)) {
            return UriComponentsBuilder.fromHttpUrl(provider.getAuthorizeUrl())
                    .queryParam("response_type", "code").queryParam("client_id", provider.getClientId())
                    .queryParam("redirect_uri", callback).queryParam("scope", "openid profile email")
                    .queryParam("state", state).build().toUriString();
        }
        if ("CAS".equals(type)) {
            return UriComponentsBuilder.fromHttpUrl(provider.getAuthorizeUrl())
                    .queryParam("service", callback).queryParam("state", state).build().toUriString();
        }
        return UriComponentsBuilder.fromHttpUrl(provider.getAuthorizeUrl())
                .queryParam("RelayState", state).queryParam("redirect_uri", callback).build().toUriString();
    }

    private void ensureEnabled(AuthUserEntity user) {
        if (!"ENABLED".equals(user.getStatus())) {
            throw new BusinessException(403, "user is disabled");
        }
    }

    private void validateTenant(Long tenantId) {
        if (tenantId == null || tenantId.longValue() <= 0L) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private String normalizeBearer(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        return authorization.startsWith("Bearer ") ? authorization.substring("Bearer ".length()) : authorization;
    }

    private String randomToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
