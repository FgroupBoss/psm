package com.fgroupboss.ai.psm.moc.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * MOC 变更状态及合法迁移辅助。
 */
public enum MocChangeStatus {
    DRAFT,
    SUBMITTED,
    INITIAL_REVIEW,
    IMPACT_ANALYSIS,
    APPROVING,
    PENDING_IMPL,
    IMPLEMENTING,
    PENDING_VERIFY,
    CLOSED,
    RETURNED,
    CANCELLED;

    public static MocChangeStatus fromCode(String code) {
        if (code == null) {
            throw new BusinessException(400, "moc change status is required");
        }
        try {
            return MocChangeStatus.valueOf(code.trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "unsupported moc change status: " + code);
        }
    }

    public static void assertEditable(String current) {
        MocChangeStatus status = fromCode(current);
        if (status != DRAFT && status != RETURNED) {
            throw new BusinessException(400, "only DRAFT or RETURNED change can be edited");
        }
    }

    public static void assertSubmit(String current) {
        MocChangeStatus status = fromCode(current);
        if (status != DRAFT && status != RETURNED) {
            throw new BusinessException(400, "only DRAFT or RETURNED can submit");
        }
    }

    public static String targetAfterSubmit() {
        return SUBMITTED.name();
    }

    public static String targetAfterImpactAnalysis() {
        return IMPACT_ANALYSIS.name();
    }

    public static String targetAfterApprove() {
        return PENDING_IMPL.name();
    }

    public static String targetAfterVerify() {
        return PENDING_VERIFY.name();
    }

    public static void assertClose(String current) {
        if (fromCode(current) != PENDING_VERIFY) {
            throw new BusinessException(400, "only PENDING_VERIFY can close");
        }
    }

    public static String targetAfterClose() {
        return CLOSED.name();
    }

    public static void assertDirectTransition(String from, String to) {
        if (DRAFT.name().equals(from) && CLOSED.name().equals(to)) {
            throw new BusinessException(400, "illegal transition DRAFT -> CLOSED");
        }
        if (CLOSED.name().equals(from) || CANCELLED.name().equals(from)) {
            throw new BusinessException(400, "terminal status cannot transition");
        }
    }
}
