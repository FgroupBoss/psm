package com.fgroupboss.ai.psm.auth.service;

import com.fgroupboss.ai.psm.auth.config.AuthProperties;
import com.fgroupboss.ai.psm.auth.model.AuthSession;
import com.fgroupboss.ai.psm.auth.model.AuthTokenResponse;
import com.fgroupboss.ai.psm.auth.model.AuthUser;
import com.fgroupboss.ai.psm.auth.model.IdentityProvider;
import com.fgroupboss.ai.psm.auth.model.LoginRequest;
import com.fgroupboss.ai.psm.auth.model.RefreshTokenRequest;
import com.fgroupboss.ai.psm.auth.model.RegisterRequest;
import com.fgroupboss.ai.psm.auth.model.SsoCallbackRequest;
import com.fgroupboss.ai.psm.auth.model.SsoLoginResponse;
import com.fgroupboss.ai.psm.auth.repository.AuthRepository;
import com.fgroupboss.ai.psm.auth.util.PasswordHasher;
import com.fgroupboss.ai.psm.common.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

@Service
public class AuthService {

    private final AuthRepository repository;
    private final PasswordHasher passwordHasher;
    private final AuthProperties properties;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(AuthRepository repository, PasswordHasher passwordHasher, AuthProperties properties) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
        this.properties = properties;
    }

    @Transactional
    public AuthUser register(RegisterRequest request) {
        validateRegister(request);
        AuthUser existed = repository.findUserByUsername(request.getTenantId(), request.getUsername());
        if (existed != null) {
            throw new BusinessException(409, "username already exists");
        }
        AuthUser user = new AuthUser();
        user.setTenantId(request.getTenantId());
        user.setUsername(request.getUsername());
        user.setDisplayName(request.getDisplayName());
        user.setMobile(request.getMobile());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordHasher.hash(request.getPassword()));
        user.setAccountType("LOCAL");
        user.setStatus("ENABLED");
        Long userId = repository.insertUser(user);
        return sanitize(repository.findUserById(request.getTenantId(), userId));
    }

    @Transactional
    public AuthTokenResponse login(LoginRequest request, String clientIp, String userAgent) {
        validateLogin(request);
        AuthUser user = repository.findUserByUsername(request.getTenantId(), request.getUsername());
        if (user == null || !passwordHasher.matches(request.getPassword(), user.getPasswordHash())) {
            repository.insertLoginLog(request.getTenantId(), null, request.getUsername(), "LOCAL", "FAILURE", "bad credentials", clientIp, userAgent);
            throw new BusinessException(401, "bad credentials");
        }
        ensureEnabled(user);
        repository.updateLastLogin(user.getTenantId(), user.getId());
        repository.insertLoginLog(user.getTenantId(), user.getId(), user.getUsername(), "LOCAL", "SUCCESS", null, clientIp, userAgent);
        return issueToken(user);
    }

    @Transactional
    public AuthTokenResponse refresh(RefreshTokenRequest request) {
        if (request == null || !StringUtils.hasText(request.getRefreshToken())) {
            throw new BusinessException(400, "refreshToken is required");
        }
        AuthSession session = repository.findSessionByRefreshToken(request.getRefreshToken());
        if (session == null || session.getRefreshExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(401, "refresh token expired");
        }
        AuthUser user = repository.findUserById(session.getTenantId(), session.getUserId());
        if (user == null) {
            throw new BusinessException(401, "user not found");
        }
        ensureEnabled(user);
        repository.revokeSession(session.getId());
        return issueToken(user);
    }

    @Transactional
    public void logout(String accessToken) {
        AuthSession session = currentSession(accessToken);
        repository.revokeSession(session.getId());
    }

    public AuthUser me(String accessToken) {
        AuthSession session = currentSession(accessToken);
        AuthUser user = repository.findUserById(session.getTenantId(), session.getUserId());
        if (user == null) {
            throw new BusinessException(401, "user not found");
        }
        ensureEnabled(user);
        return sanitize(user);
    }

    public List<IdentityProvider> providers(Long tenantId) {
        validateTenant(tenantId);
        List<IdentityProvider> providers = repository.listProviders(tenantId);
        for (IdentityProvider provider : providers) {
            provider.setClientSecret(null);
        }
        return providers;
    }

    @Transactional
    public SsoLoginResponse startSso(Long tenantId, String providerCode, String redirectAfterLogin) {
        validateTenant(tenantId);
        IdentityProvider provider = provider(tenantId, providerCode);
        String state = randomToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(properties.getSsoStateTtlSeconds());
        repository.insertSsoState(tenantId, providerCode, state, expiresAt, redirectAfterLogin);
        SsoLoginResponse response = new SsoLoginResponse();
        response.setProviderCode(providerCode);
        response.setProviderType(provider.getProviderType());
        response.setState(state);
        response.setRedirectUrl(buildRedirectUrl(provider, state));
        return response;
    }

    @Transactional
    public AuthTokenResponse ssoCallback(String providerCode, SsoCallbackRequest request, String clientIp, String userAgent) {
        if (request == null) {
            throw new BusinessException(400, "callback request is required");
        }
        validateTenant(request.getTenantId());
        IdentityProvider provider = provider(request.getTenantId(), providerCode);
        if (!repository.consumeSsoState(request.getTenantId(), providerCode, request.getState())) {
            repository.insertLoginLog(request.getTenantId(), null, request.getExternalUsername(), provider.getProviderType(), "FAILURE", "invalid sso state", clientIp, userAgent);
            throw new BusinessException(401, "invalid sso state");
        }
        String externalUserId = resolveExternalUserId(request, provider);
        AuthUser user = repository.findUserByIdentity(request.getTenantId(), providerCode, externalUserId);
        if (user == null) {
            user = createSsoUser(provider, request, externalUserId);
            repository.insertUserIdentity(user.getTenantId(), user.getId(), providerCode, externalUserId, request.getExternalUsername());
        }
        ensureEnabled(user);
        repository.updateLastLogin(user.getTenantId(), user.getId());
        repository.insertLoginLog(user.getTenantId(), user.getId(), user.getUsername(), provider.getProviderType(), "SUCCESS", null, clientIp, userAgent);
        return issueToken(user);
    }

    private AuthTokenResponse issueToken(AuthUser user) {
        LocalDateTime now = LocalDateTime.now();
        AuthSession session = new AuthSession();
        session.setTenantId(user.getTenantId());
        session.setUserId(user.getId());
        session.setAccessToken(randomToken());
        session.setRefreshToken(randomToken());
        session.setAccessExpiresAt(now.plusSeconds(properties.getTokenTtlSeconds()));
        session.setRefreshExpiresAt(now.plusSeconds(properties.getRefreshTokenTtlSeconds()));
        repository.insertSession(session);

        AuthTokenResponse response = new AuthTokenResponse();
        response.setAccessToken(session.getAccessToken());
        response.setRefreshToken(session.getRefreshToken());
        response.setAccessExpiresAt(session.getAccessExpiresAt());
        response.setRefreshExpiresAt(session.getRefreshExpiresAt());
        response.setUser(sanitize(user));
        return response;
    }

    private AuthSession currentSession(String accessToken) {
        String token = normalizeBearer(accessToken);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(401, "access token is required");
        }
        AuthSession session = repository.findSessionByAccessToken(token);
        if (session == null || session.getAccessExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(401, "access token expired");
        }
        return session;
    }

    private AuthUser createSsoUser(IdentityProvider provider, SsoCallbackRequest request, String externalUserId) {
        AuthUser user = new AuthUser();
        user.setTenantId(request.getTenantId());
        user.setUsername(provider.getProviderCode() + "_" + externalUserId);
        user.setDisplayName(StringUtils.hasText(request.getDisplayName()) ? request.getDisplayName() : request.getExternalUsername());
        user.setAccountType("SSO");
        user.setStatus("ENABLED");
        Long id = repository.insertUser(user);
        return repository.findUserById(request.getTenantId(), id);
    }

    private String resolveExternalUserId(SsoCallbackRequest request, IdentityProvider provider) {
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

    private String buildRedirectUrl(IdentityProvider provider, String state) {
        String callback = StringUtils.hasText(provider.getCallbackUrl())
                ? provider.getCallbackUrl()
                : properties.getPublicBaseUrl() + "/auth/sso/" + provider.getProviderCode() + "/callback";
        String type = provider.getProviderType() == null ? "" : provider.getProviderType().toUpperCase(Locale.ROOT);
        if ("OIDC".equals(type) || "OAUTH2".equals(type)) {
            return UriComponentsBuilder.fromHttpUrl(provider.getAuthorizeUrl())
                    .queryParam("response_type", "code")
                    .queryParam("client_id", provider.getClientId())
                    .queryParam("redirect_uri", callback)
                    .queryParam("scope", "openid profile email")
                    .queryParam("state", state)
                    .build()
                    .toUriString();
        }
        if ("CAS".equals(type)) {
            return UriComponentsBuilder.fromHttpUrl(provider.getAuthorizeUrl())
                    .queryParam("service", callback)
                    .queryParam("state", state)
                    .build()
                    .toUriString();
        }
        return UriComponentsBuilder.fromHttpUrl(provider.getAuthorizeUrl())
                .queryParam("RelayState", state)
                .queryParam("redirect_uri", callback)
                .build()
                .toUriString();
    }

    private IdentityProvider provider(Long tenantId, String providerCode) {
        if (!StringUtils.hasText(providerCode)) {
            throw new BusinessException(400, "providerCode is required");
        }
        IdentityProvider provider = repository.findProvider(tenantId, providerCode);
        if (provider == null) {
            throw new BusinessException(404, "identity provider not found");
        }
        return provider;
    }

    private AuthUser sanitize(AuthUser user) {
        if (user == null) {
            return null;
        }
        AuthUser sanitized = new AuthUser();
        sanitized.setId(user.getId());
        sanitized.setTenantId(user.getTenantId());
        sanitized.setUsername(user.getUsername());
        sanitized.setDisplayName(user.getDisplayName());
        sanitized.setMobile(user.getMobile());
        sanitized.setEmail(user.getEmail());
        sanitized.setAccountType(user.getAccountType());
        sanitized.setStatus(user.getStatus());
        sanitized.setLastLoginAt(user.getLastLoginAt());
        return sanitized;
    }

    private void ensureEnabled(AuthUser user) {
        if (!"ENABLED".equals(user.getStatus())) {
            throw new BusinessException(403, "user is disabled");
        }
    }

    private void validateRegister(RegisterRequest request) {
        if (request == null) {
            throw new BusinessException(400, "request is required");
        }
        validateTenant(request.getTenantId());
        if (!StringUtils.hasText(request.getUsername())) {
            throw new BusinessException(400, "username is required");
        }
        if (!StringUtils.hasText(request.getPassword()) || request.getPassword().length() < 8) {
            throw new BusinessException(400, "password must be at least 8 characters");
        }
    }

    private void validateLogin(LoginRequest request) {
        if (request == null) {
            throw new BusinessException(400, "request is required");
        }
        validateTenant(request.getTenantId());
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(400, "username and password are required");
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
        if (authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length());
        }
        return authorization;
    }

    private String randomToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
