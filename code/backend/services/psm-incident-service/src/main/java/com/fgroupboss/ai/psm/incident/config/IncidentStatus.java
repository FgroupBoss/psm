package com.fgroupboss.ai.psm.incident.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * 事件调查状态机。
 */
public enum IncidentStatus {

    REPORTED,
    PENDING_INVESTIGATION,
    INVESTIGATING,
    PENDING_REVIEW,
    CAPA_EXECUTING,
    CLOSED,
    CANCELLED;

    public static void assertStartInvestigation(String current) {
        if (!REPORTED.name().equals(current) && !PENDING_INVESTIGATION.name().equals(current)) {
            throw new BusinessException(400, "cannot start investigation from " + current);
        }
    }

    public static String targetAfterStartInvestigation() {
        return INVESTIGATING.name();
    }

    /**
     * 禁止 REPORTED 直接关闭等绕过调查闭环的迁移。
     */
    public static void assertDirectTransition(String from, String to) {
        if (REPORTED.name().equals(from) && CLOSED.name().equals(to)) {
            throw new BusinessException(400, "illegal transition REPORTED -> CLOSED");
        }
    }
}
