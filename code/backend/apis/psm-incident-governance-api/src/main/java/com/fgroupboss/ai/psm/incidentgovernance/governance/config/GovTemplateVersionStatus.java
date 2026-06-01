package com.fgroupboss.ai.psm.incidentgovernance.governance.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * 集团模板版本状态机。
 */
public enum GovTemplateVersionStatus {
    DRAFT, PENDING_PUBLISH, PUBLISHED, DISABLED, REVISING;

    /**
     * 校验当前版本是否允许发布。
     */
    public static void assertPublish(String current) {
        if (!DRAFT.name().equals(current) && !PENDING_PUBLISH.name().equals(current) && !REVISING.name().equals(current)) {
            throw new BusinessException(400, "cannot publish from " + current);
        }
    }

    /**
     * 发布后的目标状态。
     */
    public static String targetAfterPublish() {
        return PUBLISHED.name();
    }

    /**
     * 禁止的直连状态迁移（发布应走 assertPublish）。
     */
    public static void assertDirectTransition(String from, String to) {
        if (DRAFT.name().equals(from) && PUBLISHED.name().equals(to)) {
            return;
        }
        if (DISABLED.name().equals(from) && PUBLISHED.name().equals(to)) {
            throw new BusinessException(400, "illegal transition DISABLED -> PUBLISHED");
        }
    }
}
