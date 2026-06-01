package com.fgroupboss.ai.psm.risk.inspection.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * 检查项执行结果状态。
 */
public enum ItemResultStatus {
    NORMAL,
    ABNORMAL,
    SKIPPED;

    public static ItemResultStatus fromCode(String code) {
        if (code == null) {
            throw new BusinessException(400, "result status is required");
        }
        try {
            return ItemResultStatus.valueOf(code.trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "unsupported result status: " + code);
        }
    }
}
