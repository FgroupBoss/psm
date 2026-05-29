package com.fgroupboss.ai.psm.pha.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * PHA 项目状态及合法迁移。
 */
public enum PhaProjectStatus {
    DRAFT, ANALYZING, PENDING_REVIEW, PUBLISHED, REVIEWING, ARCHIVED, CANCELLED;

    public static PhaProjectStatus fromCode(String code) {
        if (code == null) {
            throw new BusinessException(400, "pha project status is required");
        }
        try {
            return PhaProjectStatus.valueOf(code.trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "unsupported pha project status: " + code);
        }
    }

    public static void assertEditable(String current) {
        PhaProjectStatus status = fromCode(current);
        if (status != DRAFT && status != ANALYZING) {
            throw new BusinessException(400, "only DRAFT or ANALYZING project can be edited");
        }
    }

    public static void assertSubmit(String current) {
        PhaProjectStatus status = fromCode(current);
        if (status != DRAFT && status != ANALYZING) {
            throw new BusinessException(400, "only DRAFT or ANALYZING can submit for review");
        }
    }

    public static String targetAfterSubmit(String current) {
        assertSubmit(current);
        return PENDING_REVIEW.name();
    }

    public static void assertPublish(String current) {
        if (fromCode(current) != PENDING_REVIEW) {
            throw new BusinessException(400, "only PENDING_REVIEW can publish");
        }
    }

    public static String targetAfterPublish() {
        return PUBLISHED.name();
    }

    public static void assertArchive(String current) {
        PhaProjectStatus status = fromCode(current);
        if (status != PUBLISHED && status != REVIEWING) {
            throw new BusinessException(400, "only PUBLISHED or REVIEWING can archive");
        }
    }

    public static String targetAfterArchive() {
        return ARCHIVED.name();
    }

    public static void assertCancel(String current) {
        PhaProjectStatus status = fromCode(current);
        if (status != DRAFT && status != ANALYZING) {
            throw new BusinessException(400, "only DRAFT or ANALYZING can cancel");
        }
    }

    public static String targetAfterCancel() {
        return CANCELLED.name();
    }

    public static void assertDirectTransition(String from, String to) {
        if (DRAFT.name().equals(from) && PUBLISHED.name().equals(to)) {
            throw new BusinessException(400, "illegal transition DRAFT -> PUBLISHED");
        }
        if (ARCHIVED.name().equals(from) || CANCELLED.name().equals(from)) {
            throw new BusinessException(400, "terminal status cannot transition");
        }
    }
}