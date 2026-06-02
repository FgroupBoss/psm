package com.fgroupboss.ai.psm.common;

public final class UserContextResolver {

    private UserContextResolver() {
    }

    /**
     * 从网关/身份域注入的请求头解析登录上下文（租户与用户必填）。
     */
    public static UserContext requireContext(String tenantIdHeader, String userIdHeader,
                                             String usernameHeader, String displayNameHeader) {
        UserContext context = new UserContext();
        context.setTenantId(requireTenantId(tenantIdHeader));
        context.setUserId(requireUserId(userIdHeader));
        context.setUsername(trimToNull(usernameHeader));
        context.setDisplayName(trimToNull(displayNameHeader));
        return context;
    }

    public static Long requireTenantId(String tenantIdHeader) {
        if (!hasText(tenantIdHeader)) {
            throw new BusinessException(401, "login tenant context required");
        }
        try {
            return Long.valueOf(tenantIdHeader.trim());
        } catch (NumberFormatException ex) {
            throw new BusinessException(400, "invalid tenant id in login context");
        }
    }

    public static Long requireUserId(String userIdHeader) {
        if (!hasText(userIdHeader)) {
            throw new BusinessException(401, "login user context required");
        }
        try {
            return Long.valueOf(userIdHeader.trim());
        } catch (NumberFormatException ex) {
            throw new BusinessException(400, "invalid user id in login context");
        }
    }

    public static String operator(UserContext context, String fallback) {
        if (context == null) {
            return hasText(fallback) ? fallback : "system";
        }
        return operator(
                context.getUserId() == null ? null : String.valueOf(context.getUserId()),
                context.getUsername(),
                fallback);
    }

    public static String operator(String userId, String username, String fallback) {
        if (hasText(userId)) {
            return userId;
        }
        if (hasText(username)) {
            return username;
        }
        return hasText(fallback) ? fallback : "system";
    }

    private static String trimToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
