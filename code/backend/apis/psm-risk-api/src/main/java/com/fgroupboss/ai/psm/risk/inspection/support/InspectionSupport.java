package com.fgroupboss.ai.psm.risk.inspection.support;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.springframework.util.StringUtils;

/**
 * 巡检模块通用校验与分页工具。
 */
public final class InspectionSupport {

    private InspectionSupport() {
    }

    public static void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    public static String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    public static Page normalizePage(int pageNo, int pageSize) {
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);
        return new Page(normalizedPageNo, normalizedPageSize);
    }

    public static String nextTaskNo() {
        return "INSP-" + System.currentTimeMillis();
    }

    public static final class Page {
        public final int pageNo;
        public final int pageSize;
        public final int offset;

        private Page(int pageNo, int pageSize) {
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.offset = (pageNo - 1) * pageSize;
        }
    }
}
