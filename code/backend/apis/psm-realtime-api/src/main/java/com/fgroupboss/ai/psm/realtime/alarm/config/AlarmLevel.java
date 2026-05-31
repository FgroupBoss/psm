package com.fgroupboss.ai.psm.realtime.alarm.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 报警等级比较：LEVEL_1 最严重，数值越小等级越高。
 */
public final class AlarmLevel {

    private static final Map<String, Integer> SEVERITY = new HashMap<String, Integer>();

    static {
        SEVERITY.put("LEVEL_1", 1);
        SEVERITY.put("LEVEL_2", 2);
        SEVERITY.put("LEVEL_3", 3);
        SEVERITY.put("LEVEL_4", 4);
    }

    private AlarmLevel() {
    }

    public static int severity(String level) {
        if (level == null || level.trim().isEmpty()) {
            return Integer.MAX_VALUE;
        }
        Integer value = SEVERITY.get(level.trim().toUpperCase());
        return value == null ? Integer.MAX_VALUE : value.intValue();
    }

    /** 是否达到 minLevel 及以上严重度（LEVEL_1 高于 LEVEL_2）。 */
    public static boolean meetsMinLevel(String alarmLevel, String minLevel) {
        return severity(alarmLevel) <= severity(minLevel);
    }
}
