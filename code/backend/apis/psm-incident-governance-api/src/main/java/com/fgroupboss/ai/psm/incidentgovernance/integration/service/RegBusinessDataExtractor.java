package com.fgroupboss.ai.psm.incidentgovernance.integration.service;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.client.RegRemoteClientSupport;
import com.fgroupboss.ai.psm.incidentgovernance.integration.config.RegDataDomain;
import com.fgroupboss.ai.psm.incidentgovernance.integration.service.support.RegRecordMapSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 按数据域从业务微服务抽取待上报记录（pageSize=100）。
 */
@Slf4j
@Component
public class RegBusinessDataExtractor {

    private static final ParameterizedTypeReference<ResponseVO<PageResult<Object>>> PAGE_TYPE =
            new ParameterizedTypeReference<ResponseVO<PageResult<Object>>>() {
            };

    private final RestTemplate restTemplate;
    private final String dualPreventionServiceUrl;
    private final String workPermitServiceUrl;
    private final String majorHazardServiceUrl;
    private final String alarmServiceUrl;

    public RegBusinessDataExtractor(RestTemplate restTemplate,
                                    @Value("${psm.dual-prevention-service-url:${PSM_DUAL_PREVENTION_SERVICE_URL:http://localhost:18101}}")
                                    String dualPreventionServiceUrl,
                                    @Value("${psm.work-permit-service-url:${PSM_WORK_PERMIT_SERVICE_URL:http://localhost:18088}}")
                                    String workPermitServiceUrl,
                                    @Value("${psm.major-hazard-service-url:${PSM_MAJOR_HAZARD_SERVICE_URL:http://localhost:18086}}")
                                    String majorHazardServiceUrl,
                                    @Value("${psm.alarm-service-url:${PSM_ALARM_SERVICE_URL:http://localhost:18087}}")
                                    String alarmServiceUrl) {
        this.restTemplate = restTemplate;
        this.dualPreventionServiceUrl = RegRemoteClientSupport.trimTrailingSlash(
                dualPreventionServiceUrl, "http://localhost:18101");
        this.workPermitServiceUrl = RegRemoteClientSupport.trimTrailingSlash(
                workPermitServiceUrl, "http://localhost:18088");
        this.majorHazardServiceUrl = RegRemoteClientSupport.trimTrailingSlash(
                majorHazardServiceUrl, "http://localhost:18086");
        this.alarmServiceUrl = RegRemoteClientSupport.trimTrailingSlash(
                alarmServiceUrl, "http://localhost:18087");
    }

    /**
     * 按数据域抽取源记录并转为字段映射可用的 Map 列表。
     */
    public List<Map<String, Object>> extract(Long tenantId, String dataDomain) {
        String domain = RegDataDomain.normalize(dataDomain);
        List<?> records;
        switch (domain) {
            case RegDataDomain.DUAL_PREVENTION:
                records = fetchPage(dualPreventionServiceUrl + "/api/dual-prevention/hazards", tenantId);
                break;
            case RegDataDomain.WORK_PERMIT:
                records = fetchPage(workPermitServiceUrl + "/api/work-permits", tenantId);
                break;
            case RegDataDomain.MAJOR_HAZARD:
                records = fetchPage(majorHazardServiceUrl + "/api/major-hazards", tenantId);
                break;
            case RegDataDomain.ALARM:
                records = fetchPage(alarmServiceUrl + "/api/alarms", tenantId);
                break;
            default:
                throw new BusinessException(400, "unsupported dataDomain: " + dataDomain);
        }
        return RegRecordMapSupport.toMaps(records);
    }

    private List<?> fetchPage(String basePath, Long tenantId) {
        String url = basePath + "?tenantId=" + tenantId
                + "&pageNo=1&pageSize=" + RegRemoteClientSupport.extractPageSize();
        try {
            ResponseEntity<ResponseVO<PageResult<Object>>> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, PAGE_TYPE);
            PageResult<Object> page = RegRemoteClientSupport.unwrap(response.getBody());
            if (page == null || page.getRecords() == null) {
                return Collections.emptyList();
            }
            log.info("reg extract path={} tenantId={} recordCount={}", basePath, tenantId, page.getRecords().size());
            return page.getRecords();
        } catch (RestClientException ex) {
            log.warn("reg extract failed path={} tenantId={} reason={}", basePath, tenantId, ex.getMessage());
            return Collections.emptyList();
        }
    }
}
