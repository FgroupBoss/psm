package com.fgroupboss.ai.psm.incidentgovernance.report.client;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.dto.RemoteAlarmDetailVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.dto.RemoteAlarmEventVO;
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
 * 聚合 psm-alarm-service 报警数据（best-effort）。
 */
@Slf4j
@Component
public class AlarmReportClient {

    private static final ParameterizedTypeReference<ResponseVO<PageResult<RemoteAlarmEventVO>>> PAGE_TYPE =
            new ParameterizedTypeReference<ResponseVO<PageResult<RemoteAlarmEventVO>>>() {
            };
    private static final ParameterizedTypeReference<ResponseVO<RemoteAlarmDetailVO>> DETAIL_TYPE =
            new ParameterizedTypeReference<ResponseVO<RemoteAlarmDetailVO>>() {
            };

    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public AlarmReportClient(RestTemplate restTemplate,
                             @Value("${psm.alarm-service-url:${PSM_ALARM_SERVICE_URL:http://localhost:18087}}")
                             String serviceUrl) {
        this.restTemplate = restTemplate;
        this.serviceUrl = RemoteClientSupport.trimTrailingSlash(serviceUrl, "http://localhost:18087");
    }

    public List<RemoteAlarmEventVO> listAlarms(Long tenantId) {
        try {
            List<RemoteAlarmEventVO> all = new ArrayList<RemoteAlarmEventVO>();
            int pageNo = 1;
            while (pageNo <= RemoteClientSupport.maxPages()) {
                String url = serviceUrl + "/api/alarms?tenantId=" + tenantId
                        + "&pageNo=" + pageNo + "&pageSize=" + RemoteClientSupport.defaultPageSize();
                ResponseEntity<ResponseVO<PageResult<RemoteAlarmEventVO>>> response =
                        restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, null, PAGE_TYPE);
                PageResult<RemoteAlarmEventVO> page = RemoteClientSupport.unwrap(response.getBody());
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
            log.warn("alarm report list failed tenantId={} reason={}", tenantId, ex.getMessage());
            return Collections.emptyList();
        }
    }

    public RemoteAlarmDetailVO getDetail(Long tenantId, Long alarmId) {
        try {
            String url = serviceUrl + "/api/alarms/" + alarmId + "?tenantId=" + tenantId;
            ResponseEntity<ResponseVO<RemoteAlarmDetailVO>> response =
                    restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, null, DETAIL_TYPE);
            RemoteAlarmDetailVO detail = RemoteClientSupport.unwrap(response.getBody());
            return detail == null ? new RemoteAlarmDetailVO() : detail;
        } catch (RestClientException ex) {
            log.warn("alarm detail failed tenantId={} alarmId={} reason={}", tenantId, alarmId, ex.getMessage());
            return new RemoteAlarmDetailVO();
        }
    }
}
