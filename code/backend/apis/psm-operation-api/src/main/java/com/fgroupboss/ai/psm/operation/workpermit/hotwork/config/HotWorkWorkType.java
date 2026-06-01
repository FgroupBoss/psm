package com.fgroupboss.ai.psm.operation.workpermit.hotwork.config;

public final class HotWorkWorkType {

    public static final String HOT_WORK = "HOT_WORK";

    private HotWorkWorkType() {
    }

    public static boolean isHotWork(String workType) {
        return HOT_WORK.equalsIgnoreCase(workType);
    }
}
