package com.fgroupboss.ai.psm.gateway;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Enumeration;

@RestController
public class GatewayProxyController {

    private final GatewayProperties properties;
    private final GatewayAuthService authService;
    private final RestTemplate restTemplate;

    public GatewayProxyController(GatewayProperties properties, GatewayAuthService authService, RestTemplate restTemplate) {
        this.properties = properties;
        this.authService = authService;
        this.restTemplate = restTemplate;
    }

    @RequestMapping("/auth/**")
    public ResponseEntity<String> proxyAuth(HttpServletRequest request,
                                            @RequestBody(required = false) String body) {
        return proxy(properties.getAuthServiceUrl(), request, body, null);
    }

    @RequestMapping("/api/master-data/**")
    public ResponseEntity<String> proxyMasterData(HttpServletRequest request,
                                                  @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                                  @RequestBody(required = false) String body) {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getMasterDataServiceUrl(), request, body, principal);
    }

    @RequestMapping({"/api/areas/**", "/api/areas", "/api/units/**", "/api/units",
            "/api/equipments/**", "/api/equipments", "/api/monitor-points/**", "/api/monitor-points"})
    public ResponseEntity<String> proxyBaseData(HttpServletRequest request,
                                                @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                                @RequestBody(required = false) String body) {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getMasterDataServiceUrl(), request, body, principal);
    }

    @RequestMapping({"/api/audit/**", "/api/audit"})
    public ResponseEntity<String> proxyAudit(HttpServletRequest request,
                                             @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                             @RequestBody(required = false) String body) {
        AuthPrincipal principal = authService.authenticate(authorization);
        return proxy(properties.getAuditServiceUrl(), request, body, principal);
    }

    private ResponseEntity<String> proxy(String serviceUrl, HttpServletRequest request, String body, AuthPrincipal principal) {
        URI target = targetUri(serviceUrl, request);
        HttpHeaders headers = copyHeaders(request);
        if (principal != null) {
            headers.set(UserContextHeaders.TENANT_ID, String.valueOf(principal.getTenantId()));
            headers.set(UserContextHeaders.USER_ID, String.valueOf(principal.getId()));
            headers.set(UserContextHeaders.USERNAME, principal.getUsername());
            if (StringUtils.hasText(principal.getDisplayName())) {
                headers.set(UserContextHeaders.DISPLAY_NAME, principal.getDisplayName());
            }
        }
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        if (method == null) {
            throw new BusinessException(405, "unsupported http method");
        }
        try {
            return restTemplate.exchange(target, method, new HttpEntity<String>(body, headers), String.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .headers(responseHeaders(e))
                    .body(e.getResponseBodyAsString());
        } catch (RestClientException e) {
            throw new BusinessException(502, "upstream service unavailable");
        }
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
