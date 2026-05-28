package com.fgroupboss.ai.psm.video.config;

/**
 * 作业过程监护会话状态。
 */
public final class WatchSessionStatus {

    public static final String ACTIVE = "ACTIVE";
    public static final String CLOSED = "CLOSED";

    private WatchSessionStatus() {
    }

    public static void assertActive(String status) {
        if (!ACTIVE.equals(status)) {
            throw new com.fgroupboss.ai.psm.common.BusinessException(400,
                    "watch session is not active: " + status);
        }
    }
}
