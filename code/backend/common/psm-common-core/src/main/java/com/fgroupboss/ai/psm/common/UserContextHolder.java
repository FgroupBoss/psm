package com.fgroupboss.ai.psm.common;

/**
 * 当前请求线程内的登录用户上下文，由 Web 层 {@code UserContextPopulateFilter} 从 X-PSM-* 请求头写入。
 */
public final class UserContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<UserContext>();

    private UserContextHolder() {
    }

    public static void set(UserContext context) {
        CONTEXT.set(context);
    }

    public static UserContext get() {
        return CONTEXT.get();
    }

    public static UserContext require() {
        UserContext context = CONTEXT.get();
        if (context == null || context.getTenantId() == null || context.getUserId() == null) {
            throw new BusinessException(401, "login context required");
        }
        return context;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
