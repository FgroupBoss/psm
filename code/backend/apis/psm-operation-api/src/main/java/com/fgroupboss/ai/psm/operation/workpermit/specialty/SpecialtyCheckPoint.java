package com.fgroupboss.ai.psm.operation.workpermit.specialty;

/**
 * 作业票专项门槛检查点（通用）。
 */
public enum SpecialtyCheckPoint {
    SAVE,
    SUBMIT,
    SITE_PERMIT,
    RESUME,
    MONITOR;

    public static SpecialtyCheckPoint from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("checkPoint is required");
        }
        return SpecialtyCheckPoint.valueOf(value.trim().toUpperCase());
    }
}
