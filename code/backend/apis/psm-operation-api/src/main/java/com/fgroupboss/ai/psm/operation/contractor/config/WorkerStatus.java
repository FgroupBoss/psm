package com.fgroupboss.ai.psm.operation.contractor.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * 承包商人员准入状态及合法迁移。
 */
public enum WorkerStatus {
    INCOMPLETE,
    PENDING_REVIEW,
    APPROVED,
    REJECTED,
    SUSPENDED,
    BLACKLIST,
    RESTRICTED;

    public static WorkerStatus fromCode(String code) {
        if (code == null) {
            throw new BusinessException(400, "worker access status is required");
        }
        try {
            return WorkerStatus.valueOf(code.trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "unsupported worker access status: " + code);
        }
    }

    public static void assertSubmit(String current) {
        WorkerStatus status = fromCode(current);
        if (status != INCOMPLETE && status != REJECTED && status != APPROVED) {
            throw new BusinessException(400, "only INCOMPLETE, REJECTED or APPROVED worker can submit for review");
        }
    }

    public static String targetAfterSubmit(String current) {
        assertSubmit(current);
        return PENDING_REVIEW.name();
    }

    public static void assertApprove(String current, boolean passed) {
        if (fromCode(current) != PENDING_REVIEW) {
            throw new BusinessException(400, "only PENDING_REVIEW worker can be approved or rejected");
        }
    }

    public static String targetAfterApprove(boolean passed) {
        return passed ? APPROVED.name() : REJECTED.name();
    }

    public static void assertSuspend(String current) {
        if (fromCode(current) != APPROVED && fromCode(current) != RESTRICTED) {
            throw new BusinessException(400, "only APPROVED or RESTRICTED worker can be suspended");
        }
    }

    public static void assertBlacklist(String current) {
        WorkerStatus status = fromCode(current);
        if (status != APPROVED && status != SUSPENDED && status != RESTRICTED) {
            throw new BusinessException(400, "only APPROVED, SUSPENDED or RESTRICTED worker can be blacklisted");
        }
    }

    public static void assertDirectTransition(String from, String to) {
        if (INCOMPLETE.name().equals(from) && APPROVED.name().equals(to)) {
            throw new BusinessException(400, "illegal status transition: INCOMPLETE -> APPROVED");
        }
        if (BLACKLIST.name().equals(from)) {
            throw new BusinessException(400, "blacklisted worker cannot change status");
        }
    }

    public static void assertEditable(String current) {
        WorkerStatus status = fromCode(current);
        if (status != INCOMPLETE && status != REJECTED) {
            throw new BusinessException(400, "only INCOMPLETE or REJECTED worker can be edited");
        }
    }
}
