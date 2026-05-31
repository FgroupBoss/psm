package com.fgroupboss.ai.psm.operation.workpermit.client;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.operation.client.dto.AlarmAreaActiveCheckRequest;
import com.fgroupboss.ai.psm.operation.client.dto.AlarmAreaActiveCheckResult;
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
 * 调用 psm-alarm-service 区域活跃报警检查。
 */
@Slf4j
@Component
public class AlarmAreaActiveClient {

    private static final ParameterizedTypeReference<ResponseVO<AlarmAreaActiveCheckResult>> RESPONSE_TYPE =
            new ParameterizedTypeReference<ResponseVO<AlarmAreaActiveCheckResult>>() {
            };

    private final RestTemplate restTemplate;
    private final String alarmServiceUrl;

    public AlarmAreaActiveClient(RestTemplate restTemplate,
                                 @Value("${psm.alarm-service-url:${PSM_ALARM_SERVICE_URL:http://psm-realtime:18087}}") String alarmServiceUrl) {
        this.restTemplate = restTemplate;
        this.alarmServiceUrl = trimTrailingSlash(alarmServiceUrl);
    }

    public AlarmAreaActiveCheckResult areaActiveCheck(AlarmAreaActiveCheckRequest request) {
        String url = alarmServiceUrl + "/api/alarms/area-active-check";
        HttpEntity<AlarmAreaActiveCheckRequest> entity = new HttpEntity<AlarmAreaActiveCheckRequest>(request);
        ResponseEntity<ResponseVO<AlarmAreaActiveCheckResult>> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, RESPONSE_TYPE);
        AlarmAreaActiveCheckResult result = unwrap(response.getBody());
        if (result == null) {
            throw new RestClientException("alarm area-active-check returned empty body");
        }
        return result;
    }

    private <T> T unwrap(ResponseVO<T> body) {
        if (body == null || body.getCode() != 0) {
            return null;
        }
        return body.getData();
    }

    private String trimTrailingSlash(String url) {
        if (!StringUtils.hasText(url)) {
            return "http://psm-realtime:18087";
        }
        String normalized = url.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
