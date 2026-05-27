package com.fgroupboss.ai.psm.gateway.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.gateway.AuthPrincipal;
import com.fgroupboss.ai.psm.gateway.GatewayProperties;
import com.fgroupboss.ai.psm.gateway.service.GatewayAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 实现方式：承载网关鉴权业务实现，基于 Mapper、远程客户端或支撑组件完成校验、状态流转和结果组装。
 */
@Service
@RequiredArgsConstructor
public class GatewayAuthServiceImpl implements GatewayAuthService {

    private final GatewayProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public AuthPrincipal authenticate(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            throw new BusinessException(401, "access token is required");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorization);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    properties.getAuthServiceUrl() + "/auth/me",
                    HttpMethod.GET,
                    new HttpEntity<Void>(headers),
                    String.class);
            return parsePrincipal(response.getBody());
        } catch (HttpStatusCodeException e) {
            throw new BusinessException(401, "invalid access token");
        } catch (RestClientException e) {
            throw new BusinessException(502, "auth service unavailable");
        }
    }

    public AuthPrincipal parsePrincipal(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            if (root.path("code").asInt(500) != 0) {
                throw new BusinessException(401, root.path("message").asText("invalid access token"));
            }
            JsonNode data = root.path("data");
            if (data.isMissingNode() || data.path("id").asLong(0L) <= 0L || data.path("tenantId").asLong(0L) <= 0L) {
                throw new BusinessException(401, "invalid auth response");
            }
            AuthPrincipal principal = new AuthPrincipal();
            principal.setId(data.path("id").asLong());
            principal.setTenantId(data.path("tenantId").asLong());
            principal.setUsername(data.path("username").asText(null));
            principal.setDisplayName(data.path("displayName").asText(null));
            principal.setPermissionVersion(data.path("permissionVersion").asLong(1L));
            return principal;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(401, "invalid auth response");
        }
    }
}
