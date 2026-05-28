package com.fgroupboss.ai.psm.workpermit.config;

/**
 * SIMOPS 扫描阶段。
 */
public enum SimopsScanStage {
    SUBMIT,
    APPROVE,
    PERMIT;

    public static SimopsScanStage from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("scanStage is required");
        }
        return SimopsScanStage.valueOf(value.trim().toUpperCase());
    }

    public static SimopsScanStage fromCheckPoint(PermitCheckPoint checkPoint) {
        if (checkPoint == PermitCheckPoint.SITE_PERMIT) {
            return PERMIT;
        }
        return SUBMIT;
    }
}
