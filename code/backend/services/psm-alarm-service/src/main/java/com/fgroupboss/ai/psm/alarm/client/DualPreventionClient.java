package com.fgroupboss.ai.psm.alarm.client;

import com.fgroupboss.ai.psm.common.ResponseVO;
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
 * 调用 psm-dual-prevention-service 创建隐患（best-effort，失败抛业务异常由上层处理）。
 */
@Slf4j
@Component
public class DualPreventionClient {

    private static final ParameterizedTypeReference<ResponseVO<RemoteHazardReportVO>> HAZARD_TYPE =
            new ParameterizedTypeReference<ResponseVO<RemoteHazardReportVO>>() {
            };

    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public DualPreventionClient(RestTemplate restTemplate,
                                @Value("${psm.dual-prevention-service-url:http://localhost:18101}")
                                String serviceUrl) {
        this.restTemplate = restTemplate;
        this.serviceUrl = trimTrailingSlash(serviceUrl);
    }

    public RemoteHazardReportVO createHazard(RemoteHazardCreateRequest request) {
        try {
            String url = serviceUrl + "/api/dual-prevention/hazards";
            HttpEntity<RemoteHazardCreateRequest> entity = new HttpEntity<RemoteHazardCreateRequest>(request);
            ResponseEntity<ResponseVO<RemoteHazardReportVO>> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, HAZARD_TYPE);
            ResponseVO<RemoteHazardReportVO> body = response.getBody();
            if (body == null || body.getCode() != 0 || body.getData() == null) {
                throw new IllegalStateException("dual prevention create hazard failed");
            }
            return body.getData();
        } catch (RestClientException ex) {
            log.warn("dual prevention create hazard failed sourceBizId={} reason={}",
                    request.getSourceBizId(), ex.getMessage());
            throw ex;
        }
    }

    private String trimTrailingSlash(String url) {
        if (!StringUtils.hasText(url)) {
            return "http://localhost:18101";
        }
        String normalized = url.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
