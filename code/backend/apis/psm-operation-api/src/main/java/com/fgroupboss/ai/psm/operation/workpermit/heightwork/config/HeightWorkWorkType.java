package com.fgroupboss.ai.psm.operation.workpermit.heightwork.config;

public final class HeightWorkWorkType {

    public static final String HEIGHT_WORK = "HEIGHT_WORK";

    private HeightWorkWorkType() {
    }

    public static boolean isHeightWork(String workType) {
        return HEIGHT_WORK.equalsIgnoreCase(workType);
    }
}
