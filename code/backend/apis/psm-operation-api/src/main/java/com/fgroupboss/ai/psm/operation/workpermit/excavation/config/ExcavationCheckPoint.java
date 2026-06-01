package com.fgroupboss.ai.psm.operation.workpermit.excavation.config;

/**
 * 动土作业专项门槛检查点。
 */
public enum ExcavationCheckPoint {
    SAVE,
    SUBMIT,
    SITE_PERMIT,
    RESUME,
    ACCEPTANCE;

    public static ExcavationCheckPoint from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("checkPoint is required");
        }
        return ExcavationCheckPoint.valueOf(value.trim().toUpperCase());
    }
}
