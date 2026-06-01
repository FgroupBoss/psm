package com.fgroupboss.ai.psm.operation.workpermit.tempelectric.config;

public final class TempElectricWorkType {

    public static final String TEMPORARY_ELECTRIC = "TEMPORARY_ELECTRIC";

    private TempElectricWorkType() {
    }

    public static boolean isTempElectric(String workType) {
        return TEMPORARY_ELECTRIC.equalsIgnoreCase(workType);
    }
}
