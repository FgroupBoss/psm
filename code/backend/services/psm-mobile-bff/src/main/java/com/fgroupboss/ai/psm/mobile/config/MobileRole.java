package com.fgroupboss.ai.psm.mobile.config;

/**
 * 移动端现场角色（用于待办过滤）。
 */
public enum MobileRole {

    GUARDIAN,
    PERMIT_ISSUER,
    SUPERVISOR,
    ALL;

    public static MobileRole from(String value) {
        if (value == null || value.trim().isEmpty()) {
            return ALL;
        }
        return MobileRole.valueOf(value.trim().toUpperCase());
    }
}
