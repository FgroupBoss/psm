package com.fgroupboss.ai.psm.auth.interfaces;

import com.fgroupboss.ai.psm.auth.model.AuthTokenResponse;
import com.fgroupboss.ai.psm.auth.model.AuthUser;
import com.fgroupboss.ai.psm.auth.model.IdentityProvider;
import com.fgroupboss.ai.psm.auth.model.LoginRequest;
import com.fgroupboss.ai.psm.auth.model.RefreshTokenRequest;
import com.fgroupboss.ai.psm.auth.model.RegisterRequest;
import com.fgroupboss.ai.psm.auth.model.SsoCallbackRequest;
import com.fgroupboss.ai.psm.auth.model.SsoLoginResponse;
import com.fgroupboss.ai.psm.auth.service.AuthService;
import com.fgroupboss.ai.psm.common.ResponseVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseVO<AuthUser> register(@RequestBody RegisterRequest request) {
        return ResponseVO.success(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseVO<AuthTokenResponse> login(@RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        return ResponseVO.success(authService.login(request, clientIp(servletRequest), servletRequest.getHeader("User-Agent")));
    }

    @PostMapping("/logout")
    public ResponseVO<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.logout(authorization);
        return ResponseVO.success();
    }

    @PostMapping("/token/refresh")
    public ResponseVO<AuthTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseVO.success(authService.refresh(request));
    }

    @GetMapping("/me")
    public ResponseVO<AuthUser> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ResponseVO.success(authService.me(authorization));
    }

    @GetMapping("/sso/providers")
    public ResponseVO<List<IdentityProvider>> providers(@RequestParam Long tenantId) {
        return ResponseVO.success(authService.providers(tenantId));
    }

    @GetMapping("/sso/{providerCode}/login")
    public ResponseVO<SsoLoginResponse> ssoLogin(@PathVariable String providerCode,
                                                 @RequestParam Long tenantId,
                                                 @RequestParam(required = false) String redirectAfterLogin) {
        return ResponseVO.success(authService.startSso(tenantId, providerCode, redirectAfterLogin));
    }

    @PostMapping("/sso/{providerCode}/callback")
    public ResponseVO<AuthTokenResponse> ssoCallback(@PathVariable String providerCode,
                                                     @RequestBody SsoCallbackRequest request,
                                                     HttpServletRequest servletRequest) {
        return ResponseVO.success(authService.ssoCallback(providerCode, request, clientIp(servletRequest), servletRequest.getHeader("User-Agent")));
    }

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
        return ResponseVO.success(authService.ssoCallback(providerCode, request, clientIp(servletRequest), servletRequest.getHeader("User-Agent")));
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && forwarded.trim().length() > 0) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
