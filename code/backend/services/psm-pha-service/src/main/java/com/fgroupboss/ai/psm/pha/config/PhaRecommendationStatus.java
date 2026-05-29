package com.fgroupboss.ai.psm.pha.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * PHA 建议项状态机。
 */
public enum PhaRecommendationStatus {
    PENDING_ASSIGN, RECTIFYING, PENDING_VERIFY, CLOSED, RETURNED, OVERDUE;

    public static void assertAssign(String current) {
        if (!PENDING_ASSIGN.name().equals(current) && !RETURNED.name().equals(current)) {
            throw new BusinessException(400, "only PENDING_ASSIGN or RETURNED can assign");
        }
    }

    public static String targetAfterAssign() {
        return RECTIFYING.name();
    }

    public static void assertRectify(String current) {
        if (!RECTIFYING.name().equals(current)) {
            throw new BusinessException(400, "only RECTIFYING can complete rectify");
        }
    }

    public static String targetAfterRectify() {
        return PENDING_VERIFY.name();
    }

    public static void assertVerify(String current) {
        if (!PENDING_VERIFY.name().equals(current)) {
            throw new BusinessException(400, "only PENDING_VERIFY can verify");
        }
    }

    public static String targetAfterVerify(boolean passed) {
        return passed ? CLOSED.name() : RETURNED.name();
    }
}