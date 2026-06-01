package com.fgroupboss.ai.psm.processsafety.api.common;

import com.fgroupboss.ai.psm.common.BusinessException;

import java.time.LocalDateTime;

/**
 * 流程安全域公共校验工具 — 替代 barrier/moc/pha/pssr 四份 EntitySupport 副本。
 * 不依赖服务实现，只做参数校验，可在 API 层安全共享。
 */
public final class ProcessSafetySupport {

    private ProcessSafetySupport() {
    }

    public static void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    public static void requireId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(400, "id is required");
        }
    }

    public static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BusinessException(400, fieldName + " is required");
        }
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
