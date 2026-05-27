package com.fgroupboss.ai.psm.majorhazard.config;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 危险源等级排序（LEVEL_1 最严重，数字越小等级越高）。
 */
public final class HazardLevel {

    private HazardLevel() {
    }

    public static int rank(String level) {
        if (!StringUtils.hasText(level)) {
            return Integer.MAX_VALUE;
        }
        String normalized = level.trim();
        if (normalized.startsWith("LEVEL_")) {
            try {
                return Integer.parseInt(normalized.substring(6));
            } catch (NumberFormatException ignored) {
                return Integer.MAX_VALUE;
            }
        }
        return Integer.MAX_VALUE;
    }

    public static String maxLevel(List<String> levels) {
        if (levels == null || levels.isEmpty()) {
            return null;
        }
        String selected = null;
        int selectedRank = Integer.MAX_VALUE;
        for (String level : levels) {
            int currentRank = rank(level);
            if (currentRank < selectedRank) {
                selectedRank = currentRank;
                selected = level;
            }
        }
        return selected;
    }

    public static List<String> collectLevels(List<String> levels) {
        List<String> result = new ArrayList<String>();
        if (levels == null) {
            return result;
        }
        for (String level : levels) {
            if (StringUtils.hasText(level)) {
                result.add(level.trim());
            }
        }
        return result;
    }
}
