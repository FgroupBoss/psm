package com.fgroupboss.ai.psm.incidentgovernance.report.client;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.dto.RemoteAuditLogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合 psm-audit-service 审计日志（best-effort）。
 */
@Slf4j
@Component
public class AuditReportClient {

    private static final ParameterizedTypeReference<ResponseVO<PageResult<RemoteAuditLogVO>>> PAGE_TYPE =
            new ParameterizedTypeReference<ResponseVO<PageResult<RemoteAuditLogVO>>>() {
            };

    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public AuditReportClient(RestTemplate restTemplate,
                             @Value("${psm.audit-service-url:${PSM_AUDIT_SERVICE_URL:http://localhost:18094}}")
                             String serviceUrl) {
        this.restTemplate = restTemplate;
        this.serviceUrl = RemoteClientSupport.trimTrailingSlash(serviceUrl, "http://localhost:18094");
    }

    public List<RemoteAuditLogVO> listLogs(Long tenantId) {
        try {
            List<RemoteAuditLogVO> all = new ArrayList<RemoteAuditLogVO>();
            int pageNo = 1;
            while (pageNo <= RemoteClientSupport.maxPages()) {
                String url = serviceUrl + "/api/audit/logs?tenantId=" + tenantId
                        + "&pageNo=" + pageNo + "&pageSize=" + RemoteClientSupport.defaultPageSize();
                ResponseEntity<ResponseVO<PageResult<RemoteAuditLogVO>>> response =
                        restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, null, PAGE_TYPE);
                PageResult<RemoteAuditLogVO> page = RemoteClientSupport.unwrap(response.getBody());
                if (page == null || page.getRecords() == null || page.getRecords().isEmpty()) {
                    break;
                }
                all.addAll(page.getRecords());
                if (all.size() >= page.getTotal()) {
                    break;
                }
                pageNo++;
            }
            return all;
        } catch (RestClientException ex) {
            log.warn("audit report list failed tenantId={} reason={}", tenantId, ex.getMessage());
            return Collections.emptyList();
        }
    }
}
