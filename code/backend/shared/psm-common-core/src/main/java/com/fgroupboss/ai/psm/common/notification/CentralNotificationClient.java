package com.fgroupboss.ai.psm.common.notification;

import com.fgroupboss.ai.psm.common.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 向 {@code psm-notification-service} 发送站内信（best-effort，失败不阻断业务事务）。
 */
@Component
public class CentralNotificationClient {

    private static final Logger log = LoggerFactory.getLogger(CentralNotificationClient.class);

    private static final ParameterizedTypeReference<ResponseVO<Map<String, Object>>> RESPONSE_TYPE =
            new ParameterizedTypeReference<ResponseVO<Map<String, Object>>>() {
            };

    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;
    private final boolean enabled;

    public CentralNotificationClient(RestTemplate restTemplate,
                                     @Value("${psm.notification-service-url:${PSM_NOTIFICATION_SERVICE_URL:http://localhost:18093}}")
                                             String notificationServiceUrl,
                                     @Value("${psm.central-notification-enabled:true}") boolean enabled) {
        this.restTemplate = restTemplate;
        this.notificationServiceUrl = trimTrailingSlash(notificationServiceUrl);
        this.enabled = enabled;
    }

    public void send(NotificationSendRequest request) {
        if (!enabled || request == null || request.getTenantId() == null || request.getUserId() == null) {
            return;
        }
        try {
            String url = notificationServiceUrl + "/api/notifications/send";
            HttpEntity<NotificationSendRequest> entity = new HttpEntity<NotificationSendRequest>(request);
            ResponseEntity<ResponseVO<Map<String, Object>>> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, RESPONSE_TYPE);
            if (response.getBody() == null || response.getBody().getCode() != 0) {
                log.warn("central notification send unexpected response bizType={} bizId={}",
                        request.getBizType(), request.getBizId());
            }
        } catch (RestClientException ex) {
            log.warn("central notification send failed bizType={} bizId={} reason={}",
                    request.getBizType(), request.getBizId(), ex.getMessage());
        }
    }

    public void sendToUsers(Long tenantId, List<Long> userIds, String requestIdPrefix,
                            String templateCode, String bizType, Long bizId,
                            Map<String, String> variables) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        int index = 0;
        for (Long userId : userIds) {
            if (userId == null) {
                continue;
            }
            NotificationSendRequest request = new NotificationSendRequest();
            request.setTenantId(tenantId);
            request.setUserId(userId);
            request.setTemplateCode(templateCode);
            request.setBizType(bizType);
            request.setBizId(bizId);
            if (variables != null) {
                request.getVariables().putAll(variables);
            }
            if (StringUtils.hasText(requestIdPrefix)) {
                request.setRequestId(requestIdPrefix + "-" + userId + "-" + index);
            }
            send(request);
            index++;
        }
    }

    public static NotificationSendRequest build(Long tenantId, Long userId, String requestId,
                                                String templateCode, String bizType, Long bizId,
                                                Map<String, String> variables) {
        NotificationSendRequest request = new NotificationSendRequest();
        request.setTenantId(tenantId);
        request.setUserId(userId);
        request.setRequestId(requestId);
        request.setTemplateCode(templateCode);
        request.setBizType(bizType);
        request.setBizId(bizId);
        if (variables != null) {
            request.getVariables().putAll(variables);
        }
        return request;
    }

    public static List<Long> singletonUser(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(userId);
    }

    private String trimTrailingSlash(String url) {
        if (!StringUtils.hasText(url)) {
            return "http://localhost:18093";
        }
        String normalized = url.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
