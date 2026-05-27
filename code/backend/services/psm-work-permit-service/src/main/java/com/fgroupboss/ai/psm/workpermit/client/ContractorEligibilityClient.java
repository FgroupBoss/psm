package com.fgroupboss.ai.psm.workpermit.client;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.workpermit.client.dto.ContractorEligibilityRequest;
import com.fgroupboss.ai.psm.workpermit.client.dto.ContractorEligibilityResult;
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
 * 调用 psm-contractor-service 人员准入校验。
 */
@Slf4j
@Component
public class ContractorEligibilityClient {

    private static final ParameterizedTypeReference<ResponseVO<ContractorEligibilityResult>> RESPONSE_TYPE =
            new ParameterizedTypeReference<ResponseVO<ContractorEligibilityResult>>() {
            };

    private final RestTemplate restTemplate;
    private final String contractorServiceUrl;

    public ContractorEligibilityClient(RestTemplate restTemplate,
                                       @Value("${psm.contractor-service-url:${PSM_CONTRACTOR_SERVICE_URL:http://localhost:18085}}") String contractorServiceUrl) {
        this.restTemplate = restTemplate;
        this.contractorServiceUrl = trimTrailingSlash(contractorServiceUrl);
    }

    public ContractorEligibilityResult check(ContractorEligibilityRequest request) {
        String url = contractorServiceUrl + "/api/contractors/workers/eligibility-check";
        HttpEntity<ContractorEligibilityRequest> entity = new HttpEntity<ContractorEligibilityRequest>(request);
        ResponseEntity<ResponseVO<ContractorEligibilityResult>> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, RESPONSE_TYPE);
        ContractorEligibilityResult result = unwrap(response.getBody());
        if (result == null) {
            throw new RestClientException("contractor eligibility-check returned empty body");
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
            return "http://localhost:18085";
        }
        String normalized = url.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
