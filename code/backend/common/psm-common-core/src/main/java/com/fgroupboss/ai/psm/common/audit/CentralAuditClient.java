package com.fgroupboss.ai.psm.common.audit;

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
 * 向 {@code psm-audit-service} 上报变更审计（best-effort，失败不阻断业务事务）。
 */
@Slf4j
@Component
public class CentralAuditClient {

    private static final ParameterizedTypeReference<ResponseVO<Void>> RESPONSE_TYPE =
            new ParameterizedTypeReference<ResponseVO<Void>>() {
            };

    private final RestTemplate restTemplate;
    private final String auditServiceUrl;
    private final boolean enabled;

    public CentralAuditClient(RestTemplate restTemplate,
                              @Value("${psm.audit-service-url:${PSM_AUDIT_SERVICE_URL:http://localhost:18094}}") String auditServiceUrl,
                              @Value("${psm.central-audit-enabled:true}") boolean enabled) {
        this.restTemplate = restTemplate;
        this.auditServiceUrl = trimTrailingSlash(auditServiceUrl);
        this.enabled = enabled;
    }

    public void append(AuditChangeLogIngestRequest request) {
        if (!enabled || request == null || request.getTenantId() == null) {
            return;
        }
        try {
            String url = auditServiceUrl + "/api/audit/logs";
            HttpEntity<AuditChangeLogIngestRequest> entity = new HttpEntity<AuditChangeLogIngestRequest>(request);
            ResponseEntity<ResponseVO<Void>> response = restTemplate.exchange(url, HttpMethod.POST, entity, RESPONSE_TYPE);
            if (response.getBody() == null || response.getBody().getCode() != 0) {
                log.warn("central audit append unexpected response bizType={} bizId={}",
                        request.getBizType(), request.getBizId());
            }
        } catch (RestClientException ex) {
            log.warn("central audit append failed bizType={} bizId={} reason={}",
                    request.getBizType(), request.getBizId(), ex.getMessage());
        }
    }

    public static AuditChangeLogIngestRequest build(Long tenantId, String operatorName, String action,
                                                  String bizType, Long bizId, String beforeValue, String afterValue) {
        AuditChangeLogIngestRequest request = new AuditChangeLogIngestRequest();
        request.setTenantId(tenantId);
        request.setOperatorName(StringUtils.hasText(operatorName) ? operatorName.trim() : "system");
        request.setAction(action);
        request.setBizType(bizType);
        request.setBizId(bizId);
        request.setBeforeValue(beforeValue);
        request.setAfterValue(afterValue);
        request.setResult("SUCCESS");
        return request;
    }

    private String trimTrailingSlash(String url) {
        if (!StringUtils.hasText(url)) {
            return "http://localhost:18094";
        }
        String normalized = url.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
