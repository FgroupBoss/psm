package com.fgroupboss.ai.psm.workpermit.client;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.workpermit.client.dto.DualPreventionAreaOpenCheckRequest;
import com.fgroupboss.ai.psm.workpermit.client.dto.DualPreventionAreaOpenCheckResult;
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
 * 调用 psm-dual-prevention-service 区域未销项重大隐患检查。
 */
@Slf4j
@Component
public class DualPreventionAreaHazardClient {

    private static final ParameterizedTypeReference<ResponseVO<DualPreventionAreaOpenCheckResult>> RESPONSE_TYPE =
            new ParameterizedTypeReference<ResponseVO<DualPreventionAreaOpenCheckResult>>() {
            };

    private final RestTemplate restTemplate;
    private final String dualPreventionServiceUrl;

    public DualPreventionAreaHazardClient(RestTemplate restTemplate,
                                          @Value("${psm.dual-prevention-service-url:${PSM_DUAL_PREVENTION_SERVICE_URL:http://localhost:18101}}")
                                          String dualPreventionServiceUrl) {
        this.restTemplate = restTemplate;
        this.dualPreventionServiceUrl = trimTrailingSlash(dualPreventionServiceUrl);
    }

    public DualPreventionAreaOpenCheckResult areaOpenCheck(DualPreventionAreaOpenCheckRequest request) {
        String url = dualPreventionServiceUrl + "/api/dual-prevention/hazards/area-open-check";
        HttpEntity<DualPreventionAreaOpenCheckRequest> entity = new HttpEntity<DualPreventionAreaOpenCheckRequest>(request);
        ResponseEntity<ResponseVO<DualPreventionAreaOpenCheckResult>> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, RESPONSE_TYPE);
        DualPreventionAreaOpenCheckResult result = unwrap(response.getBody());
        if (result == null) {
            throw new RestClientException("dual-prevention area-open-check returned empty body");
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
            return "http://localhost:18101";
        }
        String normalized = url.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
