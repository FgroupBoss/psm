package com.fgroupboss.ai.psm.inspection.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * 巡检任务状态及合法迁移。
 */
public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    MISSED;

    public static TaskStatus fromCode(String code) {
        if (code == null) {
            throw new BusinessException(400, "task status is required");
        }
        try {
            return TaskStatus.valueOf(code.trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "unsupported task status: " + code);
        }
    }

    public static void assertStart(String current) {
        if (fromCode(current) != PENDING) {
            throw new BusinessException(400, "only PENDING task can be started");
        }
    }

    public static void assertInProgress(String current) {
        if (fromCode(current) != IN_PROGRESS) {
            throw new BusinessException(400, "task must be IN_PROGRESS");
        }
    }

    public static void assertComplete(String current) {
        if (fromCode(current) != IN_PROGRESS) {
            throw new BusinessException(400, "only IN_PROGRESS task can be completed");
        }
    }

    public static boolean isOverdueCandidate(String current) {
        TaskStatus status = fromCode(current);
        return status == PENDING || status == IN_PROGRESS;
    }
}
