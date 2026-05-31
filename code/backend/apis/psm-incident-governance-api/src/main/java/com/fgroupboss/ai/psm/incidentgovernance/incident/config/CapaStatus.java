package com.fgroupboss.ai.psm.incidentgovernance.incident.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * CAPA 状态机。
 */
public enum CapaStatus {

    PENDING_ASSIGN,
    EXECUTING,
    PENDING_VERIFY,
    CLOSED,
    RETURNED;

    public static void assertVerify(String current) {
        if (!PENDING_VERIFY.name().equals(current) && !EXECUTING.name().equals(current)) {
            throw new BusinessException(400, "cannot verify CAPA from " + current);
        }
    }

    public static String targetAfterVerify(boolean passed) {
        return passed ? CLOSED.name() : RETURNED.name();
    }
}
