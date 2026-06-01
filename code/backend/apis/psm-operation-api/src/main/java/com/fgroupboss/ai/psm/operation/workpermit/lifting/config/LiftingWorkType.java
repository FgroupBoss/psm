package com.fgroupboss.ai.psm.operation.workpermit.lifting.config;

public final class LiftingWorkType {

    public static final String LIFTING = "LIFTING";

    private LiftingWorkType() {
    }

    public static boolean isLifting(String workType) {
        return LIFTING.equalsIgnoreCase(workType);
    }
}
