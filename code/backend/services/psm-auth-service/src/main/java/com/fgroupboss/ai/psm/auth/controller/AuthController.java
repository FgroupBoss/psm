package com.fgroupboss.ai.psm.auth.controller;

import com.fgroupboss.ai.psm.auth.model.dto.LoginRequest;
import com.fgroupboss.ai.psm.auth.model.dto.RefreshTokenRequest;
import com.fgroupboss.ai.psm.auth.model.dto.RegisterRequest;
import com.fgroupboss.ai.psm.auth.model.dto.SsoCallbackRequest;
import com.fgroupboss.ai.psm.auth.model.vo.AuthTokenResponse;
import com.fgroupboss.ai.psm.auth.model.vo.AuthUserVO;
import com.fgroupboss.ai.psm.auth.model.vo.IdentityProviderVO;
import com.fgroupboss.ai.psm.auth.model.vo.SsoLoginResponse;
import com.fgroupboss.ai.psm.auth.service.AuthService;
import com.fgroupboss.ai.psm.common.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * Authentication API endpoints for local account and SSO workflows.
 *
 * <p>All user and provider responses are view objects that omit stored credentials.</p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Creates a local tenant user account. */
    @PostMapping("/register")
    public ResponseVO<AuthUserVO> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseVO.success(authService.register(request));
    }

    /** Authenticates a local account and returns a new token session. */
    @PostMapping("/login")
    public ResponseVO<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest servletRequest) {
        return ResponseVO.success(authService.login(request, clientIp(servletRequest),
                servletRequest.getHeader("User-Agent")));
    }

    /** Revokes the active token session. */
    @PostMapping("/logout")
    public ResponseVO<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.logout(authorization);
        return ResponseVO.success();
    }

    /** Replaces a valid refresh-token session with a new token session. */
    @PostMapping("/token/refresh")
    public ResponseVO<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseVO.success(authService.refresh(request));
    }

    /** Returns the authenticated user without stored credentials. */
    @GetMapping("/me")
    public ResponseVO<AuthUserVO> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ResponseVO.success(authService.me(authorization));
    }

    /** Lists tenant identity providers without provider secrets. */
    @GetMapping("/sso/providers")
    public ResponseVO<List<IdentityProviderVO>> providers(@RequestParam Long tenantId) {
        return ResponseVO.success(authService.providers(tenantId));
    }

    /** Creates the state value and redirect URL for an SSO login. */
    @GetMapping("/sso/{providerCode}/login")
    public ResponseVO<SsoLoginResponse> ssoLogin(@PathVariable String providerCode,
                                                 @RequestParam Long tenantId,
                                                 @RequestParam(required = false) String redirectAfterLogin) {
        return ResponseVO.success(authService.startSso(tenantId, providerCode, redirectAfterLogin));
    }

    /** Handles SSO callbacks supplied as a JSON body. */
    @PostMapping("/sso/{providerCode}/callback")
    public ResponseVO<AuthTokenResponse> ssoCallback(@PathVariable String providerCode,
                                                     @Valid @RequestBody SsoCallbackRequest request,
                                                     HttpServletRequest servletRequest) {
        return ResponseVO.success(authService.ssoCallback(providerCode, request, clientIp(servletRequest),
                servletRequest.getHeader("User-Agent")));
    }

    /** Handles redirect-style SSO callbacks supplied as query parameters. */
    @GetMapping("/sso/{providerCode}/callback")
    public ResponseVO<AuthTokenResponse> ssoCallbackGet(@PathVariable String providerCode,
                                                        @RequestParam Long tenantId,
                                                        @RequestParam String state,
                                                        @RequestParam(required = false) String code,
                                                        @RequestParam(required = false) String externalUserId,
                                                        @RequestParam(required = false) String externalUsername,
                                                        HttpServletRequest servletRequest) {
        SsoCallbackRequest request = new SsoCallbackRequest();
        request.setTenantId(tenantId);
        request.setState(state);
        request.setCode(code);
        request.setExternalUserId(externalUserId);
        request.setExternalUsername(externalUsername);
        return ResponseVO.success(authService.ssoCallback(providerCode, request, clientIp(servletRequest),
                servletRequest.getHeader("User-Agent")));
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && forwarded.trim().length() > 0) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
