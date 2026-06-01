package com.fgroupboss.ai.psm.identity.auth.controller;

import com.fgroupboss.ai.psm.identity.auth.model.dto.LoginRequest;
import com.fgroupboss.ai.psm.identity.auth.model.dto.RefreshTokenRequest;
import com.fgroupboss.ai.psm.identity.auth.model.dto.RegisterRequest;
import com.fgroupboss.ai.psm.identity.auth.model.dto.SsoCallbackRequest;
import com.fgroupboss.ai.psm.identity.auth.model.vo.AuthTokenResponse;
import com.fgroupboss.ai.psm.identity.auth.model.vo.AuthUserVO;
import com.fgroupboss.ai.psm.identity.auth.model.vo.IdentityProviderVO;
import com.fgroupboss.ai.psm.identity.auth.model.vo.SsoLoginResponse;
import com.fgroupboss.ai.psm.identity.auth.service.AuthService;
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
 * 认证与会话接口。
 * <p>本地注册登录、令牌刷新、SSO 与当前用户信息。</p>
 * <p>基础路径：{@code /auth}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 注册本地账号。
     * <p>HTTP POST {@code /auth/register}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/register")
    public ResponseVO<AuthUserVO> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseVO.success(authService.register(request));
    }

    /**
     * 账号登录并签发令牌。
     * <p>HTTP POST {@code /auth/login}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/login")
    public ResponseVO<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest servletRequest) {
        return ResponseVO.success(authService.login(request, clientIp(servletRequest),
                servletRequest.getHeader("User-Agent")));
    }

    /**
     * 注销当前会话。
     * <p>HTTP POST {@code /auth/logout}</p>
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/logout")
    public ResponseVO<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.logout(authorization);
        return ResponseVO.success();
    }

    /**
     * 刷新访问令牌。
     * <p>HTTP POST {@code /auth/token/refresh}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/token/refresh")
    public ResponseVO<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseVO.success(authService.refresh(request));
    }

    /**
     * 查询me。
     * <p>HTTP GET {@code /auth/me}</p>
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/me")
    public ResponseVO<AuthUserVO> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ResponseVO.success(authService.me(authorization));
    }

    /**
     * 查询providers。
     * <p>HTTP GET {@code /auth/sso/providers}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/sso/providers")
    public ResponseVO<List<IdentityProviderVO>> providers(@RequestParam Long tenantId) {
        return ResponseVO.success(authService.providers(tenantId));
    }

    /**
     * 查询login。
     * <p>HTTP GET {@code /auth/sso/{providerCode}/login}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param providerCode providerCode 参数
     * @param tenantId 租户 ID，多租户隔离必填
     * @param redirectAfterLogin redirectAfterLogin 参数
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/sso/{providerCode}/login")
    public ResponseVO<SsoLoginResponse> ssoLogin(@PathVariable String providerCode,
                                                 @RequestParam Long tenantId,
                                                 @RequestParam(required = false) String redirectAfterLogin) {
        return ResponseVO.success(authService.startSso(tenantId, providerCode, redirectAfterLogin));
    }

    /**
     * 新增callback或触发callback相关动作。
     * <p>HTTP POST {@code /auth/sso/{providerCode}/callback}</p>
     * @param providerCode providerCode 参数
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/sso/{providerCode}/callback")
    public ResponseVO<AuthTokenResponse> ssoCallback(@PathVariable String providerCode,
                                                     @Valid @RequestBody SsoCallbackRequest request,
                                                     HttpServletRequest servletRequest) {
        return ResponseVO.success(authService.ssoCallback(providerCode, request, clientIp(servletRequest),
                servletRequest.getHeader("User-Agent")));
    }

    /**
     * 查询callback。
     * <p>HTTP GET {@code /auth/sso/{providerCode}/callback}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param providerCode providerCode 参数
     * @param tenantId 租户 ID，多租户隔离必填
     * @param state state 参数
     * @param code code 参数
     * @param externalUserId externalUser ID
     * @param externalUsername externalUsername 参数
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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
