package com.fgroupboss.ai.psm.operation.workpermit.roadbreak.config;

/**
 * 断路作业专项门槛检查点。
 */
public enum RoadBreakCheckPoint {
    SAVE,
    SUBMIT,
    SITE_PERMIT,
    MONITOR,
    ACCEPTANCE;

    public static RoadBreakCheckPoint from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("checkPoint is required");
        }
        return RoadBreakCheckPoint.valueOf(value.trim().toUpperCase());
    }
}
