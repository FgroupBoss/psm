package com.fgroupboss.ai.psm.risk.majorhazard.client;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.risk.majorhazard.client.dto.AlarmAreaActiveCheckRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.client.dto.AlarmAreaActiveCheckResult;
import com.fgroupboss.ai.psm.risk.majorhazard.client.dto.AlarmEventSummary;
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

import java.util.Collections;
import java.util.List;

/**
 * 璋冪敤 psm-alarm-service锛坆est-effort锛屽け璐ユ椂杩斿洖绌虹粨鏋滀笉闃绘柇涓绘祦绋嬶級銆? */
@Slf4j
@Component
public class AlarmServiceClient {

    private static final ParameterizedTypeReference<ResponseVO<PageResult<AlarmEventSummary>>> PAGE_TYPE =
            new ParameterizedTypeReference<ResponseVO<PageResult<AlarmEventSummary>>>() {
            };

    private static final ParameterizedTypeReference<ResponseVO<AlarmAreaActiveCheckResult>> CHECK_TYPE =
            new ParameterizedTypeReference<ResponseVO<AlarmAreaActiveCheckResult>>() {
            };

    private final RestTemplate restTemplate;
    private final String alarmServiceUrl;

    public AlarmServiceClient(RestTemplate restTemplate,
                              @Value("${psm.alarm-service-url:${PSM_ALARM_SERVICE_URL:http://psm-realtime:18087}}") String alarmServiceUrl) {
        this.restTemplate = restTemplate;
        this.alarmServiceUrl = trimTrailingSlash(alarmServiceUrl);
    }

    public List<AlarmEventSummary> listByHazard(Long tenantId, Long hazardId) {
        if (tenantId == null || hazardId == null) {
            return Collections.emptyList();
        }
        try {
            String url = alarmServiceUrl + "/api/alarms?tenantId=" + tenantId
                    + "&hazardId=" + hazardId + "&pageNo=1&pageSize=50";
            ResponseEntity<ResponseVO<PageResult<AlarmEventSummary>>> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, PAGE_TYPE);
            PageResult<AlarmEventSummary> page = unwrap(response.getBody());
            return page == null || page.getRecords() == null ? Collections.<AlarmEventSummary>emptyList() : page.getRecords();
        } catch (RestClientException ex) {
            log.warn("alarm listByHazard failed tenantId={} hazardId={} reason={}", tenantId, hazardId, ex.getMessage());
            return Collections.emptyList();
        }
    }

    public AlarmAreaActiveCheckResult areaActiveCheck(Long tenantId, Long areaId, String minLevel) {
        AlarmAreaActiveCheckResult empty = new AlarmAreaActiveCheckResult();
        if (tenantId == null || areaId == null) {
            return empty;
        }
        try {
            AlarmAreaActiveCheckRequest request = new AlarmAreaActiveCheckRequest();
            request.setTenantId(tenantId);
            request.setAreaId(areaId);
            request.setMinLevel(minLevel);
            String url = alarmServiceUrl + "/api/alarms/area-active-check";
            HttpEntity<AlarmAreaActiveCheckRequest> entity = new HttpEntity<AlarmAreaActiveCheckRequest>(request);
            ResponseEntity<ResponseVO<AlarmAreaActiveCheckResult>> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, CHECK_TYPE);
            AlarmAreaActiveCheckResult result = unwrap(response.getBody());
            return result == null ? empty : result;
        } catch (RestClientException ex) {
            log.warn("alarm areaActiveCheck failed tenantId={} areaId={} reason={}", tenantId, areaId, ex.getMessage());
            return empty;
        }
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

