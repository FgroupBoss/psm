package com.fgroupboss.ai.psm.barrier.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * 机械完整性缺陷状态。
 */
public enum MiDefectStatus {

    PENDING_CONFIRM, PENDING_REPAIR, REPAIRING, PENDING_REVIEW, CLOSED, RETURNED;

    public static void assertClose(String current) {
        if (!PENDING_REVIEW.name().equals(current)) {
            throw new BusinessException(400, "only PENDING_REVIEW defect can close");
        }
    }

    public static String targetAfterClose() {
        return CLOSED.name();
    }
}
