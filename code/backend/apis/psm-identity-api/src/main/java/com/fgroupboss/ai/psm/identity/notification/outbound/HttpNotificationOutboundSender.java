package com.fgroupboss.ai.psm.identity.notification.outbound;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.identity.notification.config.NotificationChannelProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 通过可配置 HTTP 网关对接第三方短信/邮件/IM 平台。
 * <p>请求体字段固定，网关侧按 channel 路由到具体厂商即可。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpNotificationOutboundSender implements NotificationOutboundSender {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final NotificationChannelProperties properties;

    @Override
    public String channel() {
        return "*";
    }

    public OutboundSendResult sendViaHttp(String channel, OutboundSendRequest request) {
        NotificationChannelProperties.ChannelSettings settings = properties.settingsFor(channel);
        if (settings == null || !settings.isEnabled()) {
            return OutboundSendResult.skipped("channel disabled");
        }
        if (!"HTTP".equalsIgnoreCase(settings.getMode())) {
            return OutboundSendResult.skipped("channel mode is not HTTP");
        }
        if (!StringUtils.hasText(settings.getHttpUrl())) {
            return OutboundSendResult.skipped("httpUrl not configured");
        }
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("channel", channel);
        body.put("tenantId", request.getTenantId());
        body.put("userId", request.getUserId());
        body.put("requestId", request.getRequestId());
        body.put("templateCode", request.getTemplateCode());
        body.put("title", request.getTitle());
        body.put("content", request.getContent());
        body.put("recipientPhone", request.getRecipientPhone());
        body.put("recipientEmail", request.getRecipientEmail());
        body.put("variables", request.getVariables());
        body.put("signName", settings.getSignName());
        body.put("fromAddress", settings.getFromAddress());
        body.put("corpId", settings.getCorpId());
        body.put("agentId", settings.getAgentId());
        body.put("appKey", settings.getAppKey());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (StringUtils.hasText(settings.getApiKey())) {
            headers.set("X-Api-Key", settings.getApiKey());
        }
        if (StringUtils.hasText(settings.getApiSecret())) {
            headers.set("X-Api-Secret", settings.getApiSecret());
        }
        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    settings.getHttpUrl().trim(),
                    new HttpEntity<String>(jsonBody, headers),
                    String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return OutboundSendResult.failed("HTTP_" + response.getStatusCodeValue(),
                        "gateway returned non-2xx");
            }
            String providerMsgId = parseProviderMsgId(response.getBody());
            log.info("outbound sent channel={} requestId={} providerMsgId={}",
                    channel, request.getRequestId(), providerMsgId);
            return OutboundSendResult.sent(providerMsgId);
        } catch (RestClientException ex) {
            log.warn("outbound http failed channel={} requestId={} reason={}",
                    channel, request.getRequestId(), ex.getMessage());
            return OutboundSendResult.failed("HTTP_ERROR", ex.getMessage());
        } catch (java.io.IOException ex) {
            return OutboundSendResult.failed("SERIALIZE_ERROR", ex.getMessage());
        }
    }

    @Override
    public OutboundSendResult send(OutboundSendRequest request) {
        return sendViaHttp(request.getChannel(), request);
    }

    private String parseProviderMsgId(String body) {
        if (!StringUtils.hasText(body)) {
            return null;
        }
        try {
            JsonNode json = objectMapper.readTree(body);
            if (json.hasNonNull("providerMsgId")) {
                return json.get("providerMsgId").asText();
            }
            if (json.hasNonNull("messageId")) {
                return json.get("messageId").asText();
            }
            if (json.hasNonNull("msgId")) {
                return json.get("msgId").asText();
            }
        } catch (Exception ignored) {
            // 网关可返回空 body，仅 2xx 即视为成功
        }
        return null;
    }
}
