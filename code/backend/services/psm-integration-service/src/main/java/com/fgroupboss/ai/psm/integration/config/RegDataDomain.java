package com.fgroupboss.ai.psm.integration.config;

/**
 * 监管上报数据域（与映射配置 data_domain 一致）。
 */
public final class RegDataDomain {

    public static final String DUAL_PREVENTION = "DUAL_PREVENTION";
    public static final String WORK_PERMIT = "WORK_PERMIT";
    public static final String MAJOR_HAZARD = "MAJOR_HAZARD";
    public static final String ALARM = "ALARM";

    private RegDataDomain() {
    }

    public static String normalize(String dataDomain) {
        if (dataDomain == null) {
            return "";
        }
        return dataDomain.trim().toUpperCase().replace('-', '_');
    }
}
