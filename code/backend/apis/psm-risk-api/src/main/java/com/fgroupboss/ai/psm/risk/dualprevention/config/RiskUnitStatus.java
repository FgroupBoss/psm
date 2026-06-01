package com.fgroupboss.ai.psm.risk.dualprevention.config;

/**
 * 风险单元状态：草稿、生效、停用。
 */
public enum RiskUnitStatus {

    DRAFT,
    ACTIVE,
    DISABLED;

    public static void assertEditable(String status) {
        if (!DRAFT.name().equals(status) && !ACTIVE.name().equals(status)) {
            throw new com.fgroupboss.ai.psm.common.BusinessException(400,
                    "risk unit cannot be edited in status: " + status);
        }
    }
}
