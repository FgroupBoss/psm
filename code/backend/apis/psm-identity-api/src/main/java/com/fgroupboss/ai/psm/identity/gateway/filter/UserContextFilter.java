package com.fgroupboss.ai.psm.identity.gateway.filter;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.identity.gateway.AuthPrincipal;
import com.fgroupboss.ai.psm.identity.gateway.service.GatewayAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 从 Bearer Token 解析登录用户并注入 X-PSM-* 请求头。
 * 整合后 identity 域 API 不再经网关代理，需在本服务内补齐用户上下文。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
@RequiredArgsConstructor
public class UserContextFilter extends OncePerRequestFilter {

    private final GatewayAuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (shouldSkip(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorization)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (hasUserContext(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            AuthPrincipal principal = authService.authenticate(authorization);
            filterChain.doFilter(new UserContextRequestWrapper(request, principal), response);
        } catch (BusinessException ex) {
            writeError(response, ex.getCode(), ex.getMessage());
        }
    }

    private boolean shouldSkip(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/");
    }

    private boolean hasUserContext(HttpServletRequest request) {
        return StringUtils.hasText(request.getHeader(UserContextHeaders.TENANT_ID))
                && StringUtils.hasText(request.getHeader(UserContextHeaders.USER_ID));
    }

    private void writeError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code >= 400 && code < 600 ? code : 500);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ResponseVO.failure(code, message));
    }

    private static final class UserContextRequestWrapper extends HttpServletRequestWrapper {

        private final Map<String, String> headers = new HashMap<String, String>();

        private UserContextRequestWrapper(HttpServletRequest request, AuthPrincipal principal) {
            super(request);
            headers.put(UserContextHeaders.TENANT_ID, String.valueOf(principal.getTenantId()));
            headers.put(UserContextHeaders.USER_ID, String.valueOf(principal.getId()));
            if (StringUtils.hasText(principal.getUsername())) {
                headers.put(UserContextHeaders.USERNAME, principal.getUsername());
            }
            if (StringUtils.hasText(principal.getDisplayName())) {
                headers.put(UserContextHeaders.DISPLAY_NAME, principal.getDisplayName());
            }
            if (principal.getPermissionVersion() != null) {
                headers.put(UserContextHeaders.PERMISSION_VERSION, String.valueOf(principal.getPermissionVersion()));
            }
        }

        @Override
        public String getHeader(String name) {
            String value = headers.get(name);
            return value != null ? value : super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (headers.containsKey(name)) {
                return Collections.enumeration(Collections.singletonList(headers.get(name)));
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> names = new HashSet<String>();
            Enumeration<String> parent = super.getHeaderNames();
            while (parent.hasMoreElements()) {
                names.add(parent.nextElement());
            }
            names.addAll(headers.keySet());
            return Collections.enumeration(names);
        }
    }
}
