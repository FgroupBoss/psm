package com.fgroupboss.ai.psm.common;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * 从转发 URL 中剔除 tenantId/userId 查询参数，避免前端篡改租户/用户隔离边界。
 */
public final class UserContextQueryParams {

    private UserContextQueryParams() {
    }

    public static URI stripFromForwardUrl(String baseUrl, String requestUri, String rawQuery) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + requestUri);
        if (!StringUtils.hasText(rawQuery)) {
            return builder.build(true).toUri();
        }
        MultiValueMap<String, String> params = UriComponentsBuilder.fromUriString("?" + rawQuery).build().getQueryParams();
        MultiValueMap<String, String> filtered = new LinkedMultiValueMap<String, String>();
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            if (isContextParam(entry.getKey())) {
                continue;
            }
            filtered.put(entry.getKey(), entry.getValue());
        }
        if (!filtered.isEmpty()) {
            builder.queryParams(filtered);
        }
        return builder.build(true).toUri();
    }

    private static boolean isContextParam(String name) {
        return "tenantId".equalsIgnoreCase(name) || "userId".equalsIgnoreCase(name);
    }
}
