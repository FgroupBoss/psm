package com.fgroupboss.ai.psm.auth.service;

import com.fgroupboss.ai.psm.auth.model.dto.LoginRequest;
import com.fgroupboss.ai.psm.auth.model.dto.RefreshTokenRequest;
import com.fgroupboss.ai.psm.auth.model.dto.RegisterRequest;
import com.fgroupboss.ai.psm.auth.model.dto.SsoCallbackRequest;
import com.fgroupboss.ai.psm.auth.model.vo.AuthTokenResponse;
import com.fgroupboss.ai.psm.auth.model.vo.AuthUserVO;
import com.fgroupboss.ai.psm.auth.model.vo.IdentityProviderVO;
import com.fgroupboss.ai.psm.auth.model.vo.SsoLoginResponse;

import java.util.List;

/**
 * Provides authentication operations exposed by the authentication service.
 *
 * <p>Persistence entities and credential fields are not part of this contract;
 * responses returned to callers are sanitized view objects.</p>
 */
public interface AuthService {

    /**
     * Registers a local account for a tenant.
     *
     * @param request account registration details
     * @return newly created user without credential fields
     */
    AuthUserVO register(RegisterRequest request);

    /**
     * Authenticates a local account and creates a token session.
     *
     * @param request local credentials
     * @param clientIp caller IP recorded in the login audit log
     * @param userAgent caller user agent recorded in the login audit log
     * @return issued token information and sanitized user data
     */
    AuthTokenResponse login(LoginRequest request, String clientIp, String userAgent);

    /**
     * Revokes a refresh-token session and creates a replacement session.
     *
     * @param request refresh token request
     * @return newly issued token information
     */
    AuthTokenResponse refresh(RefreshTokenRequest request);

    /**
     * Revokes the active access-token session.
     *
     * @param accessToken bearer token or raw access token
     */
    void logout(String accessToken);

    /**
     * Resolves the currently authenticated user.
     *
     * @param accessToken bearer token or raw access token
     * @return sanitized user data
     */
    AuthUserVO me(String accessToken);

    /**
     * Lists enabled SSO providers without provider secrets.
     *
     * @param tenantId tenant identifier
     * @return enabled identity providers
     */
    List<IdentityProviderVO> providers(Long tenantId);

    /**
     * Starts an SSO login attempt and creates a one-time state value.
     *
     * @param tenantId tenant identifier
     * @param providerCode provider identifier
     * @param redirectAfterLogin optional client redirect state
     * @return identity provider redirect information
     */
    SsoLoginResponse startSso(Long tenantId, String providerCode, String redirectAfterLogin);

    /**
     * Completes an SSO callback by consuming its one-time state and issuing a session.
     *
     * @param providerCode provider identifier
     * @param request SSO callback fields
     * @param clientIp caller IP recorded in the login audit log
     * @param userAgent caller user agent recorded in the login audit log
     * @return issued token information
     */
    AuthTokenResponse ssoCallback(String providerCode, SsoCallbackRequest request, String clientIp, String userAgent);
}
