package com.fgroupboss.ai.psm.location.support;

import com.fgroupboss.ai.psm.common.BusinessException;
import org.springframework.util.StringUtils;

/**
 * 定位服务通用校验与分页归一化。
 */
public final class LocationSupport {

    private LocationSupport() {
    }

    public static void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    public static String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    public static PageSpec normalizePage(int pageNo, int pageSize) {
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);
        return new PageSpec(normalizedPageNo, normalizedPageSize);
    }

    public static final class PageSpec {
        private final int pageNo;
        private final int pageSize;
        private final int offset;

        public PageSpec(int pageNo, int pageSize) {
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.offset = (pageNo - 1) * pageSize;
        }

        public int getPageNo() {
            return pageNo;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getOffset() {
            return offset;
        }
    }
}
