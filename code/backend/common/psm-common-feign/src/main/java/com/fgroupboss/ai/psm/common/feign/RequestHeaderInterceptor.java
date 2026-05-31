package com.fgroupboss.ai.psm.common.feign;

import com.fgroupboss.ai.psm.common.UserContextHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * RestTemplate 请求头透传拦截器 — 从当前 Servlet 请求上下文提取
 * tenantId / userId / username / traceId，透传到下游服务调用。
 */
public class RequestHeaderInterceptor implements ClientHttpRequestInterceptor {

    private static final String[] HEADERS_TO_FORWARD = {
            UserContextHeaders.TENANT_ID,
            UserContextHeaders.USER_ID,
            UserContextHeaders.USERNAME,
            UserContextHeaders.DISPLAY_NAME,
            UserContextHeaders.SESSION_ID,
            UserContextHeaders.PERMISSION_VERSION,
            "X-Trace-Id"
    };

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {
            HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
            for (String header : HEADERS_TO_FORWARD) {
                String value = servletRequest.getHeader(header);
                if (value != null && !value.isEmpty()) {
                    request.getHeaders().set(header, value);
                }
            }
        }
        // 始终生成或透传 TraceId
        if (!request.getHeaders().containsKey("X-Trace-Id")) {
            request.getHeaders().set("X-Trace-Id", UUID.randomUUID().toString());
        }

        return execution.execute(request, body);
    }
}
