package com.fgroupboss.ai.psm.operation.workpermit.excavation.config;

/**
 * 动土现场许可必检项。
 */
public final class ExcavationSiteCheckItems {

    public static final String[] SITE_PERMIT_REQUIRED = {
            "EXSC_LAYOUT", "EXSC_DETECT", "EXSC_TRIAL", "EXSC_SHORING",
            "EXSC_EDGE", "EXSC_PASSAGE", "EXSC_DRAIN", "EXSC_WARNING"
    };

    private ExcavationSiteCheckItems() {
    }
}
