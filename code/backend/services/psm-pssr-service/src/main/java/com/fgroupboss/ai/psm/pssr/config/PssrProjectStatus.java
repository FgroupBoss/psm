package com.fgroupboss.ai.psm.pssr.config;

import com.fgroupboss.ai.psm.common.BusinessException;

public enum PssrProjectStatus {
    DRAFT, PENDING_REVIEW, REVIEWING, RECTIFYING, PENDING_APPROVAL, APPROVED, ARCHIVED, RETURNED, CANCELLED;

    public static void assertApproveStartup(String current) {
        if (!PENDING_APPROVAL.name().equals(current)) {
            throw new BusinessException(400, "only PENDING_APPROVAL can approve startup");
        }
    }

    public static String targetAfterApproveStartup() {
        return APPROVED.name();
    }

    public static void assertRejectStartup(String current) {
        if (!PENDING_APPROVAL.name().equals(current)) {
            throw new BusinessException(400, "only PENDING_APPROVAL can reject startup");
        }
    }

    public static String targetAfterRejectStartup() {
        return RETURNED.name();
    }

    public static void assertDirectTransition(String from, String to) {
        if (DRAFT.name().equals(from) && APPROVED.name().equals(to)) {
            throw new BusinessException(400, "illegal transition DRAFT -> APPROVED");
        }
    }
}
