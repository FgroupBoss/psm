package com.fgroupboss.ai.psm.risk.dualprevention.config;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 隐患等级比较：用于区域开工检查等场景判断「达到最低等级」的阻断集合。
 */
public final class HazardLevelSupport {

    private static final String DEFAULT_MIN_LEVEL = "MAJOR";

    private static final List<String> SEVERITY_ORDER = Arrays.asList(
            "GENERAL", "一般",
            "LARGE", "较大",
            "MAJOR", "重大", "CRITICAL");

    private static final Map<String, Integer> LEVEL_RANK = buildLevelRank();

    private static final Set<String> MAJOR_AND_ABOVE = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "MAJOR", "LARGE", "较大", "重大", "CRITICAL")));

    private HazardLevelSupport() {
    }

    public static String defaultMinLevel() {
        return DEFAULT_MIN_LEVEL;
    }

    /**
     * 判断隐患等级是否达到或超过 {@code minLevel}（默认 MAJOR：较大及以上阻断）。
     */
    public static boolean meetsMinLevel(String hazardLevel, String minLevel) {
        if (!StringUtils.hasText(hazardLevel)) {
            return false;
        }
        String effectiveMin = StringUtils.hasText(minLevel) ? minLevel.trim() : DEFAULT_MIN_LEVEL;
        if (DEFAULT_MIN_LEVEL.equalsIgnoreCase(effectiveMin) && MAJOR_AND_ABOVE.contains(hazardLevel.trim())) {
            return true;
        }
        Integer hazardRank = LEVEL_RANK.get(hazardLevel.trim());
        Integer minRank = LEVEL_RANK.get(effectiveMin);
        if (hazardRank == null || minRank == null) {
            return MAJOR_AND_ABOVE.contains(hazardLevel.trim());
        }
        return hazardRank >= minRank;
    }

    private static Map<String, Integer> buildLevelRank() {
        Map<String, Integer> rank = new HashMap<String, Integer>();
        for (int i = 0; i < SEVERITY_ORDER.size(); i++) {
            rank.put(SEVERITY_ORDER.get(i), i / 2 + 1);
        }
        return Collections.unmodifiableMap(rank);
    }
}
