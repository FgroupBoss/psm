package com.fgroupboss.ai.psm.identity.gateway.controller;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.identity.gateway.AuthPrincipal;
import com.fgroupboss.ai.psm.identity.gateway.GatewayProperties;
import com.fgroupboss.ai.psm.identity.gateway.service.GatewayAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;

/**
 * 网关代理控制器 — 仅代理非 identity 域的 5 个外部域服务。
 * identity 域自身的 auth/iam/master-data/audit/file/notification
 * 路由由各自 Controller 直接处理；config 已合并至 process-safety 域，经代理转发。
 */
@RestController
@RequiredArgsConstructor
public class GatewayProxyController {

    private final GatewayProperties properties;
    private final GatewayAuthService authService;
    private final RestTemplate restTemplate;

    // ============================================================
    // 作业管控域 (psm-operation:18088)
    // ============================================================

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/contractors/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/contractors/**")
    public ResponseEntity<byte[]> proxyContractor(HttpServletRequest request,
                                                   @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getOperationControlServiceUrl(), request, principal);
    }

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/work-permits/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/work-permits/**")
    public ResponseEntity<byte[]> proxyWorkPermit(HttpServletRequest request,
                                                   @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getOperationControlServiceUrl(), request, principal);
    }

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/mobile/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/mobile/**")
    public ResponseEntity<byte[]> proxyMobile(HttpServletRequest request,
                                              @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getOperationControlServiceUrl(), request, principal);
    }

    /**
     * 代理 Web 工作台待办。
     * <p>HTTP REQUEST {@code /api/workbench/**}</p>
     */
    @RequestMapping("/api/workbench/**")
    public ResponseEntity<byte[]> proxyWorkbench(HttpServletRequest request,
                                                 @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getOperationControlServiceUrl(), request, principal);
    }

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/simops/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/simops/**")
    public ResponseEntity<byte[]> proxySimops(HttpServletRequest request,
                                              @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getOperationControlServiceUrl(), request, principal);
    }

    // ============================================================
    // 实时感知域 (psm-realtime:18087)
    // ============================================================

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/alarms/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/alarms/**")
    public ResponseEntity<byte[]> proxyAlarm(HttpServletRequest request,
                                             @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getRealtimePerceptionServiceUrl(), request, principal);
    }

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/location/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/location/**")
    public ResponseEntity<byte[]> proxyLocation(HttpServletRequest request,
                                                @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getRealtimePerceptionServiceUrl(), request, principal);
    }

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/video/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/video/**")
    public ResponseEntity<byte[]> proxyVideo(HttpServletRequest request,
                                             @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getRealtimePerceptionServiceUrl(), request, principal);
    }

    // ============================================================
    // 风险防控域 (psm-risk:18101)
    // ============================================================

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/major-hazards/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/major-hazards/**")
    public ResponseEntity<byte[]> proxyMajorHazard(HttpServletRequest request,
                                                   @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getRiskControlServiceUrl(), request, principal);
    }

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/inspection/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/inspection/**")
    public ResponseEntity<byte[]> proxyInspection(HttpServletRequest request,
                                                  @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getRiskControlServiceUrl(), request, principal);
    }

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/dual-prevention/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/dual-prevention/**")
    public ResponseEntity<byte[]> proxyDualPrevention(HttpServletRequest request,
                                                      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getRiskControlServiceUrl(), request, principal);
    }

    // ============================================================
    // 过程安全域 (psm-process-safety:18111)
    // ============================================================

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/pha/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/pha/**")
    public ResponseEntity<byte[]> proxyPha(HttpServletRequest request,
                                           @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getProcessSafetyServiceUrl(), request, principal);
    }

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/moc/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/moc/**")
    public ResponseEntity<byte[]> proxyMoc(HttpServletRequest request,
                                           @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getProcessSafetyServiceUrl(), request, principal);
    }

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/pssr/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/pssr/**")
    public ResponseEntity<byte[]> proxyPssr(HttpServletRequest request,
                                            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getProcessSafetyServiceUrl(), request, principal);
    }

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/barriers/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping({"/api/barriers/**", "/api/mechanical-integrity/**"})
    public ResponseEntity<byte[]> proxyBarrier(HttpServletRequest request,
                                               @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getProcessSafetyServiceUrl(), request, principal);
    }

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/config/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/config/**")
    public ResponseEntity<byte[]> proxyConfig(HttpServletRequest request,
                                              @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getProcessSafetyServiceUrl(), request, principal);
    }

    // ============================================================
    // 事件治理域 (psm-incident-governance:18115)
    // ============================================================

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/incidents/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/incidents/**")
    public ResponseEntity<byte[]> proxyIncident(HttpServletRequest request,
                                                @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getIncidentGovernanceServiceUrl(), request, principal);
    }

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/governance/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/governance/**")
    public ResponseEntity<byte[]> proxyGovernance(HttpServletRequest request,
                                                  @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getIncidentGovernanceServiceUrl(), request, principal);
    }

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/reports/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping({"/api/reports/**", "/api/dashboard/**", "/api/acceptance/**"})
    public ResponseEntity<byte[]> proxyReport(HttpServletRequest request,
                                              @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getIncidentGovernanceServiceUrl(), request, principal);
    }

    /**
     * 代理访问**。
     * <p>HTTP REQUEST {@code /api/integration/**}</p>
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @RequestMapping("/api/integration/**")
    public ResponseEntity<byte[]> proxyIntegration(HttpServletRequest request,
                                                   @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws IOException {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getIncidentGovernanceServiceUrl(), request, principal);
    }

    // ============================================================
    // 代理实现
    // ============================================================

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
            throw new BusinessException(502, "upstream service unavailable: " + e.getMessage());
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
        if (upstreamHeaders == null) return headers;
        if (upstreamHeaders.getContentType() != null) headers.setContentType(upstreamHeaders.getContentType());
        if (upstreamHeaders.getContentDisposition() != null) headers.setContentDisposition(upstreamHeaders.getContentDisposition());
        return headers;
    }

    private URI targetUri(String serviceUrl, HttpServletRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUrl + request.getRequestURI());
        String query = request.getQueryString();
        if (StringUtils.hasText(query)) builder.query(query);
        return builder.build(true).toUri();
    }

    private HttpHeaders copyHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (shouldSkipHeader(name)) continue;
            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) headers.add(name, values.nextElement());
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
