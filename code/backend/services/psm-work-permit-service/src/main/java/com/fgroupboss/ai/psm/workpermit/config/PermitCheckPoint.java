package com.fgroupboss.ai.psm.workpermit.config;

public enum PermitCheckPoint {
    SUBMIT,
    SITE_PERMIT;

    public static PermitCheckPoint from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("checkPoint is required");
        }
        return PermitCheckPoint.valueOf(value.trim().toUpperCase());
    }
}
