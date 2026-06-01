package com.fgroupboss.ai.psm.operation.workpermit.confinedspace.config;

public final class ConfinedSpaceWorkType {

    public static final String CONFINED_SPACE = "CONFINED_SPACE";

    private ConfinedSpaceWorkType() {
    }

    public static boolean isConfinedSpace(String workType) {
        return CONFINED_SPACE.equalsIgnoreCase(workType);
    }
}
