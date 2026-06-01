package com.fgroupboss.ai.psm.operation.workpermit.heightwork.config;

/**
 * 高处作业专项门槛检查点。
 */
public enum HeightWorkCheckPoint {
    SAVE,
    SUBMIT,
    SITE_PERMIT,
    RESUME,
    MONITOR;

    public static HeightWorkCheckPoint from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("checkPoint is required");
        }
        return HeightWorkCheckPoint.valueOf(value.trim().toUpperCase());
    }
}
