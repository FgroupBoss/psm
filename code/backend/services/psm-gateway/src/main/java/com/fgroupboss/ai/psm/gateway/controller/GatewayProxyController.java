package com.fgroupboss.ai.psm.gateway.controller;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.gateway.AuthPrincipal;
import com.fgroupboss.ai.psm.gateway.GatewayProperties;
import com.fgroupboss.ai.psm.gateway.service.GatewayAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;

/**
 * 接口用途：提供网关代理相关 HTTP API，统一封装请求校验、服务调用与响应返回。
 */
@RestController
@RequiredArgsConstructor
public class GatewayProxyController {

    private final GatewayProperties properties;
    private final GatewayAuthService authService;
    private final RestTemplate restTemplate;

    /**
     * 接口用途：处理接口请求。
     */
    @RequestMapping("/auth/**")
    public ResponseEntity<byte[]> proxyAuth(HttpServletRequest request) throws IOException {
        return proxy(properties.getAuthServiceUrl(), request, null);
    }

    /**
     * 接口用途：处理接口请求。
     */
    @RequestMapping("/api/iam/**")
    public ResponseEntity<byte[]> proxyIam(HttpServletRequest request,
                                           @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getIamServiceUrl(), request, principal);
    }

    /**
     * 接口用途：处理接口请求。
     */
    @RequestMapping("/api/config/**")
    public ResponseEntity<byte[]> proxyConfigRule(HttpServletRequest request,
                                                   @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getConfigRuleServiceUrl(), request, principal);
    }

    /**
     * 接口用途：处理接口请求。
     */
    @RequestMapping("/api/master-data/**")
    public ResponseEntity<byte[]> proxyMasterData(HttpServletRequest request,
                                                  @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getMasterDataServiceUrl(), request, principal);
    }

    @RequestMapping({"/api/areas/**", "/api/areas", "/api/units/**", "/api/units",
            "/api/equipments/**", "/api/equipments", "/api/monitor-points/**", "/api/monitor-points"})
    public ResponseEntity<byte[]> proxyBaseData(HttpServletRequest request,
                                                @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getMasterDataServiceUrl(), request, principal);
    }

    /**
     * 接口用途：处理接口请求。
     */
    @RequestMapping({"/api/audit/**", "/api/audit"})
    public ResponseEntity<byte[]> proxyAudit(HttpServletRequest request,
                                             @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getAuditServiceUrl(), request, principal);
    }

    /**
     * 接口用途：处理接口请求。
     */
    @RequestMapping("/api/contractors/**")
    public ResponseEntity<byte[]> proxyContractor(HttpServletRequest request,
                                                   @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getContractorServiceUrl(), request, principal);
    }

    /**
     * 接口用途：处理接口请求。
     */
    @RequestMapping("/api/major-hazards/**")
    public ResponseEntity<byte[]> proxyMajorHazard(HttpServletRequest request,
                                                   @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getMajorHazardServiceUrl(), request, principal);
    }

    /**
     * 接口用途：处理接口请求。
     */
    @RequestMapping("/api/alarms/**")
    public ResponseEntity<byte[]> proxyAlarm(HttpServletRequest request,
                                             @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getAlarmServiceUrl(), request, principal);
    }

    /**
     * 接口用途：处理接口请求。
     */
    @RequestMapping("/api/work-permits/**")
    public ResponseEntity<byte[]> proxyWorkPermit(HttpServletRequest request,
                                                   @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getWorkPermitServiceUrl(), request, principal);
    }

    /**
     * 接口用途：处理接口请求。
     */
    @RequestMapping("/api/mobile/**")
    public ResponseEntity<byte[]> proxyMobile(HttpServletRequest request,
                                              @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getMobileBffUrl(), request, principal);
    }

    /**
     * 接口用途：处理接口请求。
     */
    @RequestMapping({"/api/reports/**", "/api/dashboard/**", "/api/acceptance/**"})
    public ResponseEntity<byte[]> proxyReport(HttpServletRequest request,
                                              @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getReportServiceUrl(), request, principal);
    }

    /**
     * 接口用途：处理接口请求。
     */
    @RequestMapping("/api/files/**")
    public ResponseEntity<byte[]> proxyFile(HttpServletRequest request,
                                            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getFileServiceUrl(), request, principal);
    }

    /**
     * 接口用途：转发消息中心请求。
     */
    @RequestMapping("/api/notifications/**")
    public ResponseEntity<byte[]> proxyNotification(HttpServletRequest request,
                                                    @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getNotificationServiceUrl(), request, principal);
    }

    private ResponseEntity<byte[]> proxy(String serviceUrl, HttpServletRequest request, AuthPrincipal principal) throws IOException {
        URI target = targetUri(serviceUrl, request);
        HttpHeaders headers = copyHeaders(request);
        if (principal != null) {
            headers.set(UserContextHeaders.TENANT_ID, String.valueOf(principal.getTenantId()));
            headers.set(UserContextHeaders.USER_ID, String.valueOf(principal.getId()));
            headers.set(UserContextHeaders.USERNAME, principal.getUsername());
            if (StringUtils.hasText(principal.getDisplayName())) {
                headers.set(UserContextHeaders.DISPLAY_NAME, principal.getDisplayName());
            }
            if (principal.getPermissionVersion() != null) {
                headers.set(UserContextHeaders.PERMISSION_VERSION, String.valueOf(principal.getPermissionVersion()));
            }
        }
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        if (method == null) {
            throw new BusinessException(405, "unsupported http method");
        }
        byte[] body = readBody(request, method);
        try {
            ResponseEntity<byte[]> upstream = restTemplate.exchange(target, method, new HttpEntity<byte[]>(body, headers), byte[].class);
            return ResponseEntity.status(upstream.getStatusCode())
                    .headers(filterResponseHeaders(upstream.getHeaders()))
                    .body(upstream.getBody());
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .headers(responseHeaders(e))
                    .body(e.getResponseBodyAsByteArray());
        } catch (RestClientException e) {
            throw new BusinessException(502, "upstream service unavailable");
        }
    }

    private byte[] readBody(HttpServletRequest request, HttpMethod method) throws IOException {
        if (method == HttpMethod.GET || method == HttpMethod.HEAD || method == HttpMethod.DELETE) {
            return null;
        }
        return org.springframework.util.StreamUtils.copyToByteArray(request.getInputStream());
    }

    private HttpHeaders filterResponseHeaders(HttpHeaders upstreamHeaders) {
        HttpHeaders headers = new HttpHeaders();
        if (upstreamHeaders == null) {
            return headers;
        }
        if (upstreamHeaders.getContentType() != null) {
            headers.setContentType(upstreamHeaders.getContentType());
        }
        if (upstreamHeaders.getContentDisposition() != null) {
            headers.setContentDisposition(upstreamHeaders.getContentDisposition());
        }
        return headers;
    }

    private URI targetUri(String serviceUrl, HttpServletRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUrl + request.getRequestURI());
        String query = request.getQueryString();
        if (StringUtils.hasText(query)) {
            builder.query(query);
        }
        return builder.build(true).toUri();
    }

    private HttpHeaders copyHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (shouldSkipHeader(name)) {
                continue;
            }
            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                headers.add(name, values.nextElement());
            }
        }
        return headers;
    }

    private boolean shouldSkipHeader(String name) {
        return HttpHeaders.HOST.equalsIgnoreCase(name)
                || HttpHeaders.CONTENT_LENGTH.equalsIgnoreCase(name)
                || UserContextHeaders.TENANT_ID.equalsIgnoreCase(name)
                || UserContextHeaders.USER_ID.equalsIgnoreCase(name)
                || UserContextHeaders.USERNAME.equalsIgnoreCase(name)
                || UserContextHeaders.DISPLAY_NAME.equalsIgnoreCase(name)
                || UserContextHeaders.PERMISSION_VERSION.equalsIgnoreCase(name)
                || UserContextHeaders.SESSION_ID.equalsIgnoreCase(name);
    }

    private HttpHeaders responseHeaders(HttpStatusCodeException e) {
        HttpHeaders headers = new HttpHeaders();
        HttpHeaders upstreamHeaders = e.getResponseHeaders();
        if (upstreamHeaders != null && upstreamHeaders.getContentType() != null) {
            headers.setContentType(upstreamHeaders.getContentType());
        }
        return headers;
    }
}
