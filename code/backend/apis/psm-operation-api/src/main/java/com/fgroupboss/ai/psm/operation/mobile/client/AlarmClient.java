package com.fgroupboss.ai.psm.operation.mobile.client;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.realtime.api.alarm.dto.AlarmActionRequest;
import com.fgroupboss.ai.psm.realtime.api.alarm.vo.AlarmEventVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * 调用 psm-alarm-service 报警接口。
 */
@Slf4j
@Component
public class AlarmClient {

    private static final ParameterizedTypeReference<ResponseVO<PageResult<AlarmEventVO>>> PAGE_TYPE =
            new ParameterizedTypeReference<ResponseVO<PageResult<AlarmEventVO>>>() {
            };

    private static final ParameterizedTypeReference<ResponseVO<AlarmEventVO>> EVENT_TYPE =
            new ParameterizedTypeReference<ResponseVO<AlarmEventVO>>() {
            };

    private final RestTemplate restTemplate;
    private final String alarmServiceUrl;

    public AlarmClient(RestTemplate restTemplate,
                       @Value("${psm.alarm-service-url:${PSM_ALARM_SERVICE_URL:http://localhost:18087}}")
                       String alarmServiceUrl) {
        this.restTemplate = restTemplate;
        this.alarmServiceUrl = trimTrailingSlash(alarmServiceUrl);
    }

    public List<AlarmEventVO> listPending(Long tenantId, int pageSize) {
        if (tenantId == null) {
            return Collections.emptyList();
        }
        try {
            String url = alarmServiceUrl + "/api/alarms?tenantId=" + tenantId
                    + "&status=IN_PROGRESS&pageNo=1&pageSize=" + pageSize;
            ResponseEntity<ResponseVO<PageResult<AlarmEventVO>>> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, PAGE_TYPE);
            PageResult<AlarmEventVO> page = unwrap(response.getBody());
            return page == null || page.getRecords() == null
                    ? Collections.<AlarmEventVO>emptyList() : page.getRecords();
        } catch (RestClientException ex) {
            log.warn("alarm listPending failed tenantId={} reason={}", tenantId, ex.getMessage());
            return Collections.emptyList();
        }
    }

    public AlarmEventVO feedback(Long tenantId, Long id, AlarmActionRequest request, HttpHeaders contextHeaders) {
        String url = alarmServiceUrl + "/api/alarms/" + id + "/feedback?tenantId=" + tenantId;
        HttpEntity<AlarmActionRequest> entity = new HttpEntity<AlarmActionRequest>(request, contextHeaders);
        try {
            ResponseEntity<ResponseVO<AlarmEventVO>> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, EVENT_TYPE);
            AlarmEventVO result = unwrapRequired(response.getBody(), "alarm feedback");
            if (result == null) {
                throw new BusinessException(502, "alarm feedback returned empty body");
            }
            return result;
        } catch (HttpStatusCodeException ex) {
            log.warn("alarm feedback failed status={} body={}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            throw new BusinessException(ex.getRawStatusCode(), "alarm feedback failed");
        } catch (RestClientException ex) {
            log.error("alarm feedback call error alarmId={}", id, ex);
            throw new BusinessException(502, "alarm service unavailable");
        }
    }

    private <T> T unwrap(ResponseVO<T> body) {
        if (body == null || body.getCode() != 0) {
            return null;
        }
        return body.getData();
    }

    private <T> T unwrapRequired(ResponseVO<T> body, String action) {
        if (body == null) {
            return null;
        }
        if (body.getCode() != 0) {
            throw new BusinessException(body.getCode(), body.getMessage());
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
