package com.fgroupboss.ai.psm.operation.workpermit.excavation.config;

public final class ExcavationWorkType {

    public static final String EXCAVATION = "EXCAVATION";

    private ExcavationWorkType() {
    }

    public static boolean isExcavation(String workType) {
        return EXCAVATION.equalsIgnoreCase(workType);
    }
}
