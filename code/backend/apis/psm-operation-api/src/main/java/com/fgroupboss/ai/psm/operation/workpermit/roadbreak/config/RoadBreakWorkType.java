package com.fgroupboss.ai.psm.operation.workpermit.roadbreak.config;

public final class RoadBreakWorkType {

    public static final String ROAD_BREAK = "ROAD_BREAK";

    private RoadBreakWorkType() {
    }

    public static boolean isRoadBreak(String workType) {
        return ROAD_BREAK.equalsIgnoreCase(workType);
    }
}
