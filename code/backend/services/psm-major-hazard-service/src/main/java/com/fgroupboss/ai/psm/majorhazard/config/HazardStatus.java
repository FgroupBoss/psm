package com.fgroupboss.ai.psm.majorhazard.config;

import com.fgroupboss.ai.psm.common.BusinessException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 重大危险源状态及合法迁移（草稿 + 发布，不接审批流）。
 */
public enum HazardStatus {
    DRAFT,
    PUBLISHED,
    SUSPENDED,
    MAINTENANCE,
    ABNORMAL;

    private static final Set<String> OPERATIONAL = new HashSet<String>(Arrays.asList(
            PUBLISHED.name(), SUSPENDED.name(), MAINTENANCE.name(), ABNORMAL.name()));

    public static HazardStatus fromCode(String code) {
        if (code == null) {
            throw new BusinessException(400, "hazard status is required");
        }
        try {
            return HazardStatus.valueOf(code.trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "unsupported hazard status: " + code);
        }
    }

    public static void assertEditable(String current) {
        if (fromCode(current) != DRAFT) {
            throw new BusinessException(400, "only DRAFT hazard can be edited");
        }
    }

    public static void assertPublish(String current) {
        if (fromCode(current) != DRAFT) {
            throw new BusinessException(400, "only DRAFT hazard can be published");
        }
    }

    /**
     * 已发布态之间的状态切换（停用/检修/异常/恢复发布）。
     */
    public static void assertStatusChange(String current, String target) {
        HazardStatus from = fromCode(current);
        HazardStatus to = fromCode(target);
        if (from == DRAFT) {
            throw new BusinessException(400, "draft hazard must be published before status change");
        }
        if (to == DRAFT) {
            throw new BusinessException(400, "cannot change hazard status to DRAFT");
        }
        if (!OPERATIONAL.contains(from.name()) || !OPERATIONAL.contains(to.name())) {
            throw new BusinessException(400, "illegal hazard status transition");
        }
    }
}
