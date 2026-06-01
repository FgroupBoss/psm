package com.fgroupboss.ai.psm.operation.workpermit.blindplate.config;

/**
 * 盲板抽堵专项门槛检查点。
 */
public enum BlindPlateCheckPoint {
    SAVE,
    SUBMIT,
    SITE_PERMIT,
    ACCEPTANCE;

    public static BlindPlateCheckPoint from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("checkPoint is required");
        }
        return BlindPlateCheckPoint.valueOf(value.trim().toUpperCase());
    }
}
