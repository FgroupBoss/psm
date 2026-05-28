package com.fgroupboss.ai.psm.location.client;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.location.client.dto.AlarmEventSummary;
import com.fgroupboss.ai.psm.location.client.dto.AlarmIngestPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 调用 psm-alarm-service 事件接入（best-effort，失败不阻断定位事件落库）。
 */
@Slf4j
@Component
public class AlarmServiceClient {

    private static final ParameterizedTypeReference<ResponseVO<AlarmEventSummary>> INGEST_TYPE =
            new ParameterizedTypeReference<ResponseVO<AlarmEventSummary>>() {
            };

    private final RestTemplate restTemplate;
    private final String alarmServiceUrl;

    public AlarmServiceClient(RestTemplate restTemplate,
                              @Value("${psm.alarm-service-url:${PSM_ALARM_SERVICE_URL:http://localhost:18087}}")
                              String alarmServiceUrl) {
        this.restTemplate = restTemplate;
        this.alarmServiceUrl = trimTrailingSlash(alarmServiceUrl);
    }

    public Long ingest(AlarmIngestPayload payload) {
        if (payload == null || payload.getTenantId() == null) {
            return null;
        }
        try {
            String url = alarmServiceUrl + "/api/alarms/ingest";
            HttpEntity<AlarmIngestPayload> entity = new HttpEntity<AlarmIngestPayload>(payload);
            ResponseEntity<ResponseVO<AlarmEventSummary>> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, INGEST_TYPE);
            AlarmEventSummary summary = unwrap(response.getBody());
            return summary == null ? null : summary.getId();
        } catch (RestClientException ex) {
            log.warn("alarm ingest failed tenantId={} sourceCode={} reason={}",
                    payload.getTenantId(), payload.getSourceCode(), ex.getMessage());
            return null;
        }
    }

    private AlarmEventSummary unwrap(ResponseVO<AlarmEventSummary> body) {
        if (body == null || body.getCode() != 0) {
            return null;
        }
        return body.getData();
    }

    private String trimTrailingSlash(String url) {
        if (!StringUtils.hasText(url)) {
            return "http://localhost:18087";
        }
        String normalized = url.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
