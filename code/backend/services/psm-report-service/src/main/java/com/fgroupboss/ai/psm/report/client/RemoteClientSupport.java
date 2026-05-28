package com.fgroupboss.ai.psm.report.client;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 远程服务调用公共辅助（best-effort，失败返回空集合）。
 */
public final class RemoteClientSupport {

    private static final int MAX_PAGES = 50;
    private static final int PAGE_SIZE = 200;

    private RemoteClientSupport() {
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

    static <T> List<T> mergePages(PageResult<T> firstPage) {
        if (firstPage == null || firstPage.getRecords() == null) {
            return Collections.emptyList();
        }
        List<T> merged = new ArrayList<T>(firstPage.getRecords());
        return merged;
    }

    static int defaultPageSize() {
        return PAGE_SIZE;
    }

    static int maxPages() {
        return MAX_PAGES;
    }
}
