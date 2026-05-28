package com.fgroupboss.ai.psm.video.service.support;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 视频服务通用校验与分页辅助。
 */
public final class VideoServiceSupport {

    private VideoServiceSupport() {
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

    public static String joinCameraIds(List<Long> cameraIds) {
        if (cameraIds == null || cameraIds.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Long cameraId : cameraIds) {
            if (cameraId == null) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append(cameraId);
        }
        return builder.toString();
    }

    public static List<Long> parseCameraIds(String cameraIds) {
        if (!StringUtils.hasText(cameraIds)) {
            return Collections.emptyList();
        }
        String[] parts = cameraIds.split(",");
        List<Long> result = new ArrayList<Long>();
        for (String part : parts) {
            if (!StringUtils.hasText(part)) {
                continue;
            }
            try {
                result.add(Long.valueOf(part.trim()));
            } catch (NumberFormatException ignored) {
                // skip invalid token
            }
        }
        return result;
    }

    public static final class Page {
        public final int pageNo;
        public final int pageSize;
        public final int offset;

        public Page(int pageNo, int pageSize) {
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.offset = (pageNo - 1) * pageSize;
        }
    }

    public static <T> PageResult<T> emptyPage(Page page) {
        return new PageResult<T>(0, page.pageNo, page.pageSize, new ArrayList<T>());
    }
}
