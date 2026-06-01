package com.fgroupboss.ai.psm.operation.workpermit.roadbreak.config;

/**
 * 断路现场布控必检项。
 */
public final class RoadBreakSiteControlItems {

    public static final String[] SITE_PERMIT_REQUIRED = {
            "RBSC_BARRIER", "RBSC_SIGN", "RBSC_FENCE",
            "RBSC_LIGHT", "RBSC_DETOUR", "RBSC_EMERGENCY"
    };

    private RoadBreakSiteControlItems() {
    }
}
