package com.fgroupboss.ai.psm.operation.workpermit.blindplate.config;

public final class BlindPlateWorkType {

    public static final String BLIND_PLATE = "BLIND_PLATE";

    private BlindPlateWorkType() {
    }

    public static boolean isBlindPlate(String workType) {
        return BLIND_PLATE.equalsIgnoreCase(workType);
    }
}
