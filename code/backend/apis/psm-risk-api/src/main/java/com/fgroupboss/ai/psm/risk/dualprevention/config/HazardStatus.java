package com.fgroupboss.ai.psm.risk.dualprevention.config;

/**
 * 隐患状态。
 */
public enum HazardStatus {

    PENDING_CONFIRM,
    RECTIFYING,
    PENDING_REVIEW,
    CLOSED,
    RETURNED;

    public static boolean isTerminal(String status) {
        return CLOSED.name().equals(status);
    }

    public static boolean isOpen(String status) {
        return !isTerminal(status);
    }
}
