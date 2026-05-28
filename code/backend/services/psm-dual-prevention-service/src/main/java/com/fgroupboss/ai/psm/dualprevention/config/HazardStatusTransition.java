package com.fgroupboss.ai.psm.dualprevention.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * 隐患状态流转校验。
 */
public final class HazardStatusTransition {

    private HazardStatusTransition() {
    }

    public static void assertConfirm(String current) {
        if (!HazardStatus.PENDING_CONFIRM.name().equals(current)) {
            throw new BusinessException(400, "hazard can only be confirmed from PENDING_CONFIRM, current: " + current);
        }
    }

    public static void assertRectify(String current) {
        if (!HazardStatus.RECTIFYING.name().equals(current)
                && !HazardStatus.RETURNED.name().equals(current)) {
            throw new BusinessException(400, "hazard can only be rectified from RECTIFYING or RETURNED, current: "
                    + current);
        }
    }

    public static void assertReview(String current) {
        if (!HazardStatus.PENDING_REVIEW.name().equals(current)) {
            throw new BusinessException(400, "hazard can only be reviewed from PENDING_REVIEW, current: " + current);
        }
    }

    public static String targetAfterConfirm(boolean returned) {
        return returned ? HazardStatus.RETURNED.name() : HazardStatus.RECTIFYING.name();
    }

    public static String targetAfterReview(boolean passed) {
        return passed ? HazardStatus.CLOSED.name() : HazardStatus.RETURNED.name();
    }
}
