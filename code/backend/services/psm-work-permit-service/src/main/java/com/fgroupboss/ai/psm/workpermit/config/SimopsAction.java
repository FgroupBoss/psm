package com.fgroupboss.ai.psm.workpermit.config;

/**
 * SIMOPS 冲突处置策略。
 */
public enum SimopsAction {
    WARN,
    COORDINATE,
    BLOCK;

    public static SimopsAction from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("action is required");
        }
        return SimopsAction.valueOf(value.trim().toUpperCase());
    }

    public static int severityRank(SimopsAction action) {
        if (action == BLOCK) {
            return 3;
        }
        if (action == COORDINATE) {
            return 2;
        }
        return 1;
    }

    public static SimopsAction max(SimopsAction left, SimopsAction right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        return severityRank(left) >= severityRank(right) ? left : right;
    }
}
