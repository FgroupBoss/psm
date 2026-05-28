package com.fgroupboss.ai.psm.integration.client;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import org.springframework.util.StringUtils;

/**
 * 监管抽取远程调用辅助（best-effort）。
 */
public final class RegRemoteClientSupport {

    private static final int EXTRACT_PAGE_SIZE = 100;

    private RegRemoteClientSupport() {
    }

    public static String trimTrailingSlash(String url, String defaultUrl) {
        if (!StringUtils.hasText(url)) {
            return defaultUrl;
        }
        String normalized = url.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    public static <T> T unwrap(ResponseVO<T> body) {
        if (body == null || body.getCode() != 0) {
            return null;
        }
        return body.getData();
    }

    public static int extractPageSize() {
        return EXTRACT_PAGE_SIZE;
    }
}
