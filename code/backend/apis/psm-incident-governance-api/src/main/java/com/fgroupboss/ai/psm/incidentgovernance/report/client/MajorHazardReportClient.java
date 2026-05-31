package com.fgroupboss.ai.psm.incidentgovernance.report.client;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.dto.RemoteMajorHazardVO;
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
 * 聚合 psm-major-hazard-service 重大危险源数据（best-effort）。
 */
@Slf4j
@Component
public class MajorHazardReportClient {

    private static final ParameterizedTypeReference<ResponseVO<PageResult<RemoteMajorHazardVO>>> PAGE_TYPE =
            new ParameterizedTypeReference<ResponseVO<PageResult<RemoteMajorHazardVO>>>() {
            };

    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public MajorHazardReportClient(RestTemplate restTemplate,
                                   @Value("${psm.major-hazard-service-url:${PSM_MAJOR_HAZARD_SERVICE_URL:http://localhost:18086}}")
                                   String serviceUrl) {
        this.restTemplate = restTemplate;
        this.serviceUrl = RemoteClientSupport.trimTrailingSlash(serviceUrl, "http://localhost:18086");
    }

    public List<RemoteMajorHazardVO> listHazards(Long tenantId) {
        try {
            List<RemoteMajorHazardVO> all = new ArrayList<RemoteMajorHazardVO>();
            int pageNo = 1;
            while (pageNo <= RemoteClientSupport.maxPages()) {
                String url = serviceUrl + "/api/major-hazards?tenantId=" + tenantId
                        + "&pageNo=" + pageNo + "&pageSize=" + RemoteClientSupport.defaultPageSize();
                ResponseEntity<ResponseVO<PageResult<RemoteMajorHazardVO>>> response =
                        restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, null, PAGE_TYPE);
                PageResult<RemoteMajorHazardVO> page = RemoteClientSupport.unwrap(response.getBody());
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
            log.warn("major hazard report list failed tenantId={} reason={}", tenantId, ex.getMessage());
            return Collections.emptyList();
        }
    }
}
