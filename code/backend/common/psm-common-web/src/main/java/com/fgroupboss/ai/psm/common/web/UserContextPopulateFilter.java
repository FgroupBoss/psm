package com.fgroupboss.ai.psm.common.web;

import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextHolder;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 将 X-PSM-* 登录头写入 {@link UserContextHolder}，供非 Controller 层读取。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 30)
public class UserContextPopulateFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (shouldSkip(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String tenantHeader = request.getHeader(UserContextHeaders.TENANT_ID);
        String userHeader = request.getHeader(UserContextHeaders.USER_ID);
        if (!StringUtils.hasText(tenantHeader) || !StringUtils.hasText(userHeader)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            UserContext context = UserContextResolver.requireContext(
                    tenantHeader,
                    userHeader,
                    request.getHeader(UserContextHeaders.USERNAME),
                    request.getHeader(UserContextHeaders.DISPLAY_NAME));
            UserContextHolder.set(context);
            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }

    private boolean shouldSkip(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/");
    }
}
