package com.fgroupboss.ai.psm.processsafety.pssr.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * PSSR 审查问题状态。
 */
public enum PssrIssueStatus {

    PENDING_RECTIFY, RECTIFYING, PENDING_REVIEW, CLOSED, RETURNED;

    public static void assertRectify(String current) {
        if (!PENDING_RECTIFY.name().equals(current) && !RETURNED.name().equals(current)) {
            throw new BusinessException(400, "issue cannot rectify from " + current);
        }
    }

    public static String targetAfterRectify() {
        return RECTIFYING.name();
    }

    public static void assertReview(String current) {
        if (!RECTIFYING.name().equals(current) && !PENDING_REVIEW.name().equals(current)) {
            throw new BusinessException(400, "issue cannot review from " + current);
        }
    }

    public static String targetAfterReview(boolean passed) {
        return passed ? CLOSED.name() : RETURNED.name();
    }

    public static boolean isOpen(String status) {
        return !CLOSED.name().equals(status);
    }
}
