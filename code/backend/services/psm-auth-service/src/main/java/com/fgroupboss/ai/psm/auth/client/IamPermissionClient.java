package com.fgroupboss.ai.psm.auth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.auth.config.AuthProperties;
import com.fgroupboss.ai.psm.common.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * IAM 权限版本查询客户端。
 *
 * <p>IAM 不可用时返回默认版本，保证本地认证 demo 不被内部服务启动顺序阻断。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IamPermissionClient {

    private static final long DEFAULT_PERMISSION_VERSION = 1L;

    private final AuthProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public long currentPermissionVersion(Long tenantId, Long userId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(properties.getIamServiceUrl() + "/internal/iam/users/" + userId + "/permission-version")
                .queryParam("tenantId", tenantId)
                .build()
                .toUriString();
        try {
            String body = restTemplate.getForObject(url, String.class);
            return parseVersion(body);
        } catch (BusinessException e) {
            throw e;
        } catch (RestClientException e) {
            log.warn("iam permission version unavailable, tenantId={}, userId={}", tenantId, userId);
            return DEFAULT_PERMISSION_VERSION;
        }
    }

    private long parseVersion(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            if (root.path("code").asInt(500) != 0) {
                throw new BusinessException(401, root.path("message").asText("permission version invalid"));
            }
            long version = root.path("data").asLong(DEFAULT_PERMISSION_VERSION);
            return version <= 0L ? DEFAULT_PERMISSION_VERSION : version;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(401, "permission version invalid");
        }
    }
}
