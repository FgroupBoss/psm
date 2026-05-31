package com.fgroupboss.ai.psm.incidentgovernance.report.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.RemoteClientSupport;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.Phase3ReportSummaryVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.service.Phase3ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class Phase3ReportServiceImpl implements Phase3ReportService {

    private static final String DATA_SOURCE = "psm-report-service phase3 aggregate via RestTemplate";

    private static final ParameterizedTypeReference<ResponseVO<Map<String, Object>>> MAP_TYPE =
            new ParameterizedTypeReference<ResponseVO<Map<String, Object>>>() {
            };
    private static final ParameterizedTypeReference<ResponseVO<PageResult<Map<String, Object>>>> PAGE_TYPE =
            new ParameterizedTypeReference<ResponseVO<PageResult<Map<String, Object>>>>() {
            };

    private final RestTemplate restTemplate;
    private final String governanceServiceUrl;
    private final String phaServiceUrl;
    private final String mocServiceUrl;
    private final String pssrServiceUrl;
    private final String barrierServiceUrl;
    private final String incidentServiceUrl;

    public Phase3ReportServiceImpl(RestTemplate restTemplate,
                                   @Value("${psm.governance-service-url:${PSM_GOVERNANCE_SERVICE_URL:http://localhost:18116}}")
                                   String governanceServiceUrl,
                                   @Value("${psm.pha-service-url:${PSM_PHA_SERVICE_URL:http://localhost:18111}}")
                                   String phaServiceUrl,
                                   @Value("${psm.moc-service-url:${PSM_MOC_SERVICE_URL:http://localhost:18112}}")
                                   String mocServiceUrl,
                                   @Value("${psm.pssr-service-url:${PSM_PSSR_SERVICE_URL:http://localhost:18113}}")
                                   String pssrServiceUrl,
                                   @Value("${psm.barrier-service-url:${PSM_BARRIER_SERVICE_URL:http://localhost:18114}}")
                                   String barrierServiceUrl,
                                   @Value("${psm.incident-service-url:${PSM_INCIDENT_SERVICE_URL:http://localhost:18115}}")
                                   String incidentServiceUrl) {
        this.restTemplate = restTemplate;
        this.governanceServiceUrl = RemoteClientSupport.trimTrailingSlash(governanceServiceUrl, "http://localhost:18116");
        this.phaServiceUrl = RemoteClientSupport.trimTrailingSlash(phaServiceUrl, "http://localhost:18111");
        this.mocServiceUrl = RemoteClientSupport.trimTrailingSlash(mocServiceUrl, "http://localhost:18112");
        this.pssrServiceUrl = RemoteClientSupport.trimTrailingSlash(pssrServiceUrl, "http://localhost:18113");
        this.barrierServiceUrl = RemoteClientSupport.trimTrailingSlash(barrierServiceUrl, "http://localhost:18114");
        this.incidentServiceUrl = RemoteClientSupport.trimTrailingSlash(incidentServiceUrl, "http://localhost:18115");
    }

    @Override
    public Phase3ReportSummaryVO summary(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
        Phase3ReportSummaryVO vo = new Phase3ReportSummaryVO();
        vo.setTenantId(tenantId);
        vo.setGeneratedAt(LocalDateTime.now());
        vo.setDataSource(DATA_SOURCE);
        vo.setGovernanceDashboard(loadGovernanceDashboard(tenantId));
        vo.setPha(loadPageCount(phaServiceUrl + "/api/pha/projects?tenantId=" + tenantId + "&pageNo=1&pageSize=1", "phaProjects"));
        vo.setMoc(loadPageCount(mocServiceUrl + "/api/moc/changes?tenantId=" + tenantId + "&pageNo=1&pageSize=1", "mocChanges"));
        vo.setPssr(loadPageCount(pssrServiceUrl + "/api/pssr/projects?tenantId=" + tenantId + "&pageNo=1&pageSize=1", "pssrProjects"));
        vo.setBarrier(loadPageCount(barrierServiceUrl + "/api/barriers?tenantId=" + tenantId + "&pageNo=1&pageSize=1", "barriers"));
        vo.setIncident(loadPageCount(incidentServiceUrl + "/api/incidents?tenantId=" + tenantId + "&pageNo=1&pageSize=1", "incidents"));
        return vo;
    }

    private Map<String, Object> loadGovernanceDashboard(Long tenantId) {
        String url = governanceServiceUrl + "/api/governance/dashboard/overview?tenantId=" + tenantId;
        try {
            ResponseVO<Map<String, Object>> body = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, null, MAP_TYPE).getBody();
            if (body != null && body.getData() != null) {
                return body.getData();
            }
        } catch (RestClientException e) {
            log.warn("phase3 governance dashboard unavailable tenantId={}", tenantId, e);
        }
        Map<String, Object> fallback = new HashMap<String, Object>();
        fallback.put("available", false);
        return fallback;
    }

    private Map<String, Object> loadPageCount(String url, String label) {
        Map<String, Object> section = new HashMap<String, Object>();
        try {
            ResponseVO<PageResult<Map<String, Object>>> body = restTemplate.exchange(
                    url, org.springframework.http.HttpMethod.GET, null, PAGE_TYPE).getBody();
            if (body != null && body.getData() != null) {
                section.put("total", body.getData().getTotal());
                section.put("available", true);
                return section;
            }
        } catch (RestClientException e) {
            log.warn("phase3 {} summary unavailable url={}", label, url, e);
        }
        section.put("available", false);
        section.put("total", 0L);
        return section;
    }
}
