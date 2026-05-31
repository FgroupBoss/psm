package com.fgroupboss.ai.psm.realtime.video.client;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.realtime.video.client.dto.RemoteAlarmEventSummary;
import com.fgroupboss.ai.psm.realtime.video.client.dto.RemoteAlarmIngestRequest;
import com.fgroupboss.ai.psm.realtime.video.model.entity.VideoAiEventEntity;
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

import java.time.ZoneId;
import java.util.Date;

/**
 * 调用 psm-alarm-service 转发高等级 AI 事件（best-effort，失败不阻断主流程）。
 */
@Slf4j
@Component
public class AlarmServiceClient {

    private static final String SOURCE_TYPE = "VIDEO_AI";

    private static final ParameterizedTypeReference<ResponseVO<RemoteAlarmEventSummary>> INGEST_TYPE =
            new ParameterizedTypeReference<ResponseVO<RemoteAlarmEventSummary>>() {
            };

    private final RestTemplate restTemplate;
    private final String alarmServiceUrl;
    private final boolean centralAlarmEnabled;

    public AlarmServiceClient(RestTemplate restTemplate,
                              @Value("${psm.alarm-service-url:${PSM_ALARM_SERVICE_URL:http://localhost:18087}}") String alarmServiceUrl,
                              @Value("${psm.central-alarm-enabled:${PSM_CENTRAL_ALARM_ENABLED:true}}") boolean centralAlarmEnabled) {
        this.restTemplate = restTemplate;
        this.alarmServiceUrl = trimTrailingSlash(alarmServiceUrl);
        this.centralAlarmEnabled = centralAlarmEnabled;
    }

    /**
     * 将 AI 事件转发至报警中心，成功时返回报警 ID。
     */
    public Long forwardAiEvent(VideoAiEventEntity event) {
        if (!centralAlarmEnabled || event == null) {
            return null;
        }
        try {
            RemoteAlarmIngestRequest request = buildRequest(event);
            String url = alarmServiceUrl + "/api/alarms/ingest";
            HttpEntity<RemoteAlarmIngestRequest> entity = new HttpEntity<RemoteAlarmIngestRequest>(request);
            ResponseEntity<ResponseVO<RemoteAlarmEventSummary>> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, INGEST_TYPE);
            RemoteAlarmEventSummary summary = unwrap(response.getBody());
            if (summary == null || summary.getId() == null) {
                log.warn("alarm ingest returned empty eventNo={}", event.getEventNo());
                return null;
            }
            log.info("alarm forwarded eventNo={} alarmId={}", event.getEventNo(), summary.getId());
            return summary.getId();
        } catch (RestClientException ex) {
            log.warn("alarm ingest failed eventNo={} reason={}", event.getEventNo(), ex.getMessage());
            return null;
        }
    }

    private RemoteAlarmIngestRequest buildRequest(VideoAiEventEntity event) {
        RemoteAlarmIngestRequest request = new RemoteAlarmIngestRequest();
        request.setTenantId(event.getTenantId());
        request.setSourceType(SOURCE_TYPE);
        request.setSourceCode(event.getEventNo());
        request.setTitle(StringUtils.hasText(event.getTitle()) ? event.getTitle() : event.getEventType());
        request.setContent(event.getDescription());
        request.setAlarmLevel(mapSeverityToAlarmLevel(event.getSeverity()));
        request.setAreaId(event.getAreaId());
        request.setHazardId(event.getMajorHazardId());
        if (event.getEventTime() != null) {
            request.setOccurredAt(Date.from(event.getEventTime().atZone(ZoneId.systemDefault()).toInstant()));
        }
        return request;
    }

    private String mapSeverityToAlarmLevel(String severity) {
        if (!StringUtils.hasText(severity)) {
            return "MEDIUM";
        }
        String normalized = severity.trim().toUpperCase();
        if ("CRITICAL".equals(normalized)) {
            return "CRITICAL";
        }
        if ("HIGH".equals(normalized)) {
            return "HIGH";
        }
        if ("LOW".equals(normalized)) {
            return "LOW";
        }
        return "MEDIUM";
    }

    private <T> T unwrap(ResponseVO<T> body) {
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
