package com.fgroupboss.ai.psm.report.client;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteWorkPermitDetailVO;
import com.fgroupboss.ai.psm.report.client.dto.RemoteWorkPermitVO;
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
 * 聚合 psm-work-permit-service 作业票数据（best-effort）。
 */
@Slf4j
@Component
public class WorkPermitReportClient {

    private static final ParameterizedTypeReference<ResponseVO<PageResult<RemoteWorkPermitVO>>> PAGE_TYPE =
            new ParameterizedTypeReference<ResponseVO<PageResult<RemoteWorkPermitVO>>>() {
            };
    private static final ParameterizedTypeReference<ResponseVO<RemoteWorkPermitDetailVO>> DETAIL_TYPE =
            new ParameterizedTypeReference<ResponseVO<RemoteWorkPermitDetailVO>>() {
            };

    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public WorkPermitReportClient(RestTemplate restTemplate,
                                  @Value("${psm.work-permit-service-url:${PSM_WORK_PERMIT_SERVICE_URL:http://localhost:18088}}")
                                  String serviceUrl) {
        this.restTemplate = restTemplate;
        this.serviceUrl = RemoteClientSupport.trimTrailingSlash(serviceUrl, "http://localhost:18088");
    }

    public List<RemoteWorkPermitVO> listPermits(Long tenantId) {
        try {
            List<RemoteWorkPermitVO> all = new ArrayList<RemoteWorkPermitVO>();
            int pageNo = 1;
            while (pageNo <= RemoteClientSupport.maxPages()) {
                String url = serviceUrl + "/api/work-permits?tenantId=" + tenantId
                        + "&pageNo=" + pageNo + "&pageSize=" + RemoteClientSupport.defaultPageSize();
                ResponseEntity<ResponseVO<PageResult<RemoteWorkPermitVO>>> response =
                        restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, null, PAGE_TYPE);
                PageResult<RemoteWorkPermitVO> page = RemoteClientSupport.unwrap(response.getBody());
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
            log.warn("work permit report list failed tenantId={} reason={}", tenantId, ex.getMessage());
            return Collections.emptyList();
        }
    }

    public RemoteWorkPermitDetailVO getDetail(Long tenantId, Long permitId) {
        try {
            String url = serviceUrl + "/api/work-permits/" + permitId + "?tenantId=" + tenantId;
            ResponseEntity<ResponseVO<RemoteWorkPermitDetailVO>> response =
                    restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, null, DETAIL_TYPE);
            RemoteWorkPermitDetailVO detail = RemoteClientSupport.unwrap(response.getBody());
            return detail == null ? emptyDetail() : detail;
        } catch (RestClientException ex) {
            log.warn("work permit detail failed tenantId={} permitId={} reason={}", tenantId, permitId, ex.getMessage());
            return emptyDetail();
        }
    }

    private RemoteWorkPermitDetailVO emptyDetail() {
        return new RemoteWorkPermitDetailVO();
    }
}
