package com.fgroupboss.ai.psm.workpermit.config;

/**
 * 作业票状态（一期动火/受限空间闭环）。
 */
public enum WorkPermitStatus {
    DRAFT,
    APPROVING,
    RETURNED,
    PENDING_SITE_PERMIT,
    IN_PROGRESS,
    SUSPENDED,
    PENDING_ACCEPTANCE,
    CLOSED;

    public static WorkPermitStatus from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("status is required");
        }
        return WorkPermitStatus.valueOf(value.trim().toUpperCase());
    }
}
