package com.fgroupboss.ai.psm.contractor.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * 承包商单位准入状态及合法迁移（与 PRD / 落地设计对齐）。
 */
public enum CompanyStatus {
    DRAFT,
    PENDING_REVIEW,
    APPROVED,
    REJECTED,
    SUSPENDED,
    BLACKLIST;

    public static CompanyStatus fromCode(String code) {
        if (code == null) {
            throw new BusinessException(400, "company status is required");
        }
        try {
            return CompanyStatus.valueOf(code.trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "unsupported company status: " + code);
        }
    }

    /**
     * 提交审核：待提交/已退回/已准入(待复审) -> 待审核。
     */
    public static void assertSubmit(String current) {
        CompanyStatus status = fromCode(current);
        if (status != DRAFT && status != REJECTED && status != APPROVED) {
            throw new BusinessException(400, "only DRAFT, REJECTED or APPROVED company can submit for review");
        }
    }

    public static String targetAfterSubmit(String current) {
        assertSubmit(current);
        return PENDING_REVIEW.name();
    }

    public static void assertApprove(String current, boolean passed) {
        if (fromCode(current) != PENDING_REVIEW) {
            throw new BusinessException(400, "only PENDING_REVIEW company can be approved or rejected");
        }
    }

    public static String targetAfterApprove(boolean passed) {
        return passed ? APPROVED.name() : REJECTED.name();
    }

    public static void assertSuspend(String current) {
        if (fromCode(current) != APPROVED) {
            throw new BusinessException(400, "only APPROVED company can be suspended");
        }
    }

    public static void assertBlacklist(String current) {
        CompanyStatus status = fromCode(current);
        if (status != APPROVED && status != SUSPENDED) {
            throw new BusinessException(400, "only APPROVED or SUSPENDED company can be blacklisted");
        }
    }

    /**
     * 禁止 DRAFT 直接变为 APPROVED 等绕过审核的迁移。
     */
    public static void assertDirectTransition(String from, String to) {
        if (DRAFT.name().equals(from) && APPROVED.name().equals(to)) {
            throw new BusinessException(400, "illegal status transition: DRAFT -> APPROVED");
        }
        if (BLACKLIST.name().equals(from)) {
            throw new BusinessException(400, "blacklisted company cannot change status");
        }
    }

    public static void assertEditable(String current) {
        CompanyStatus status = fromCode(current);
        if (status != DRAFT && status != REJECTED) {
            throw new BusinessException(400, "only DRAFT or REJECTED company can be edited");
        }
    }
}
