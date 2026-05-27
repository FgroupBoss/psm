package com.fgroupboss.ai.psm.report.client;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteContractorCompanyVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteContractorWorkerVO;
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
 * 聚合 psm-contractor-service 承包商数据（best-effort）。
 */
@Slf4j
@Component
public class ContractorReportClient {

    private static final ParameterizedTypeReference<ResponseVO<PageResult<RemoteContractorCompanyVO>>> COMPANY_PAGE_TYPE =
            new ParameterizedTypeReference<ResponseVO<PageResult<RemoteContractorCompanyVO>>>() {
            };
    private static final ParameterizedTypeReference<ResponseVO<PageResult<RemoteContractorWorkerVO>>> WORKER_PAGE_TYPE =
            new ParameterizedTypeReference<ResponseVO<PageResult<RemoteContractorWorkerVO>>>() {
            };

    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public ContractorReportClient(RestTemplate restTemplate,
                                  @Value("${psm.contractor-service-url:${PSM_CONTRACTOR_SERVICE_URL:http://localhost:18085}}")
                                  String serviceUrl) {
        this.restTemplate = restTemplate;
        this.serviceUrl = RemoteClientSupport.trimTrailingSlash(serviceUrl, "http://localhost:18085");
    }

    public List<RemoteContractorCompanyVO> listCompanies(Long tenantId) {
        return fetchPage(serviceUrl + "/api/contractors/companies?tenantId=" + tenantId, COMPANY_PAGE_TYPE);
    }

    public List<RemoteContractorWorkerVO> listWorkers(Long tenantId) {
        return fetchPage(serviceUrl + "/api/contractors/workers?tenantId=" + tenantId, WORKER_PAGE_TYPE);
    }

    private <T> List<T> fetchPage(String baseUrl, ParameterizedTypeReference<ResponseVO<PageResult<T>>> type) {
        try {
            List<T> all = new ArrayList<T>();
            int pageNo = 1;
            while (pageNo <= RemoteClientSupport.maxPages()) {
                String url = baseUrl + "&pageNo=" + pageNo + "&pageSize=" + RemoteClientSupport.defaultPageSize();
                ResponseEntity<ResponseVO<PageResult<T>>> response =
                        restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, null, type);
                PageResult<T> page = RemoteClientSupport.unwrap(response.getBody());
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
            log.warn("contractor report list failed url={} reason={}", baseUrl, ex.getMessage());
            return Collections.emptyList();
        }
    }
}
