package com.fgroupboss.ai.psm.risk.inspection.client;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.risk.inspection.client.dto.HazardCreatePayload;
import com.fgroupboss.ai.psm.risk.inspection.client.dto.HazardCreateResult;
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
 * 调用 psm-dual-prevention-service 隐患上报。
 */
@Slf4j
@Component
public class DualPreventionClient {

    private static final ParameterizedTypeReference<ResponseVO<HazardCreateResult>> HAZARD_TYPE =
            new ParameterizedTypeReference<ResponseVO<HazardCreateResult>>() {
            };

    private final RestTemplate restTemplate;
    private final String dualPreventionServiceUrl;

    public DualPreventionClient(RestTemplate restTemplate,
                                @Value("${psm.dual-prevention-service-url:${PSM_DUAL_PREVENTION_SERVICE_URL:http://localhost:18101}}")
                                String dualPreventionServiceUrl) {
        this.restTemplate = restTemplate;
        this.dualPreventionServiceUrl = trimTrailingSlash(dualPreventionServiceUrl);
    }

    public HazardCreateResult createHazard(HazardCreatePayload payload) {
        if (payload == null || payload.getTenantId() == null) {
            return null;
        }
        try {
            String url = dualPreventionServiceUrl + "/api/dual-prevention/hazards";
            HttpEntity<HazardCreatePayload> entity = new HttpEntity<HazardCreatePayload>(payload);
            ResponseEntity<ResponseVO<HazardCreateResult>> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, HAZARD_TYPE);
            return unwrap(response.getBody());
        } catch (RestClientException ex) {
            log.warn("dual-prevention hazard create failed tenantId={} sourceBizId={} reason={}",
                    payload.getTenantId(), payload.getSourceBizId(), ex.getMessage());
            throw ex;
        }
    }

    private HazardCreateResult unwrap(ResponseVO<HazardCreateResult> body) {
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
