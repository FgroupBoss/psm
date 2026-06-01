package com.fgroupboss.ai.psm.risk.majorhazard.config;

import com.fgroupboss.ai.psm.common.BusinessException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 包保责任三类负责人类型。
 */
public enum ResponsibilityType {
    PRIMARY,
    TECHNICAL,
    OPERATION;

    public static final List<String> REQUIRED_TYPES = Arrays.asList(
            PRIMARY.name(), TECHNICAL.name(), OPERATION.name());

    private static final Set<String> ALL = new HashSet<String>(REQUIRED_TYPES);

    public static void assertValid(String type) {
        if (type == null || !ALL.contains(type.trim())) {
            throw new BusinessException(400, "unsupported responsibility type: " + type);
        }
    }
}
