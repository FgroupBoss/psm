package com.fgroupboss.ai.psm.common;

public final class UserContextResolver {

    private UserContextResolver() {
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

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
