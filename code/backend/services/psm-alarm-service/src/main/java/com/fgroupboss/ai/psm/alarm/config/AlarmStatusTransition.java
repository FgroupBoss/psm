package com.fgroupboss.ai.psm.alarm.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * 报警状态流转规则（批次 3）。
 */
public final class AlarmStatusTransition {

    private AlarmStatusTransition() {
    }

    public static AlarmStatus confirmTarget(AlarmStatus current) {
        if (current == AlarmStatus.NEW || current == AlarmStatus.ESCALATED) {
            return AlarmStatus.CONFIRMED;
        }
        throw illegalTransition(current, "confirm");
    }

    public static AlarmStatus dispatchTarget(AlarmStatus current) {
        if (current == AlarmStatus.CONFIRMED || current == AlarmStatus.ESCALATED) {
            return AlarmStatus.IN_PROGRESS;
        }
        throw illegalTransition(current, "dispatch");
    }

    public static AlarmStatus feedbackTarget(AlarmStatus current) {
        if (current == AlarmStatus.IN_PROGRESS) {
            return AlarmStatus.PENDING_REVIEW;
        }
        throw illegalTransition(current, "feedback");
    }

    public static AlarmStatus closeTarget(AlarmStatus current) {
        if (current == AlarmStatus.PENDING_REVIEW) {
            return AlarmStatus.CLOSED;
        }
        throw illegalTransition(current, "close");
    }

    public static AlarmStatus falseCloseTarget(AlarmStatus current) {
        if (current == AlarmStatus.NEW || current == AlarmStatus.CONFIRMED
                || current == AlarmStatus.IN_PROGRESS || current == AlarmStatus.ESCALATED) {
            return AlarmStatus.FALSE_CLOSED;
        }
        throw illegalTransition(current, "false-close");
    }

    public static void assertActionable(AlarmStatus current) {
        if (current == AlarmStatus.CLOSED || current == AlarmStatus.FALSE_CLOSED) {
            throw new BusinessException(409, "alarm already closed");
        }
    }

    private static BusinessException illegalTransition(AlarmStatus current, String action) {
        return new BusinessException(409, "cannot " + action + " from status " + current.name());
    }
}
