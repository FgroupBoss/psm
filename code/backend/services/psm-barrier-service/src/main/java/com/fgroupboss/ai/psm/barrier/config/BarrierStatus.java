package com.fgroupboss.ai.psm.barrier.config;

import com.fgroupboss.ai.psm.common.BusinessException;

public enum BarrierStatus {
    NORMAL, DEGRADED, FAILED, RESTORING, DISABLED;

    public static void assertDegrade(String current) {
        if (!NORMAL.name().equals(current) && !DEGRADED.name().equals(current)) {
            throw new BusinessException(400, "only NORMAL or DEGRADED can degrade further");
        }
    }

    public static String targetAfterDegrade(String current) {
        return NORMAL.name().equals(current) ? DEGRADED.name() : FAILED.name();
    }

    public static void assertRestore(String current) {
        if (!DEGRADED.name().equals(current) && !FAILED.name().equals(current) && !RESTORING.name().equals(current)) {
            throw new BusinessException(400, "barrier cannot restore from " + current);
        }
    }

    public static String targetAfterRestore() {
        return NORMAL.name();
    }

    public static void assertDirectTransition(String from, String to) {
        if (DISABLED.name().equals(from) && NORMAL.name().equals(to)) {
            throw new BusinessException(400, "DISABLED barrier must be enabled first");
        }
    }
}
