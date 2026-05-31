package com.fgroupboss.ai.psm.incidentgovernance.report.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.client.RemoteClientSupport;
import com.fgroupboss.ai.psm.incidentgovernance.report.model.vo.Phase2ReportSummaryVO;
import com.fgroupboss.ai.psm.incidentgovernance.report.service.Phase2ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通过 RestTemplate 聚合二期各业务服务统计指标（best-effort）。
 */
@Slf4j
@Service
public class Phase2ReportServiceImpl implements Phase2ReportService {

    private static final String DATA_SOURCE = "psm-report-service phase2 aggregate via RestTemplate";

    private static final ParameterizedTypeReference<ResponseVO<Map<String, Object>>> MAP_TYPE =
            new ParameterizedTypeReference<ResponseVO<Map<String, Object>>>() {
            };
    private static final ParameterizedTypeReference<ResponseVO<PageResult<Map<String, Object>>>> PAGE_TYPE =
            new ParameterizedTypeReference<ResponseVO<PageResult<Map<String, Object>>>>() {
            };

    private final RestTemplate restTemplate;
    private final String dualPreventionServiceUrl;
    private final String inspectionServiceUrl;
    private final String locationServiceUrl;
    private final String videoServiceUrl;
    private final String workPermitServiceUrl;

    public Phase2ReportServiceImpl(RestTemplate restTemplate,
                                   @Value("${psm.dual-prevention-service-url:${PSM_DUAL_PREVENTION_SERVICE_URL:http://psm-risk:18101}}")
                                   String dualPreventionServiceUrl,
                                   @Value("${psm.inspection-service-url:${PSM_INSPECTION_SERVICE_URL:http://psm-risk:18101}}")
                                   String inspectionServiceUrl,
                                   @Value("${psm.location-service-url:${PSM_LOCATION_SERVICE_URL:http://psm-realtime:18087}}")
                                   String locationServiceUrl,
                                   @Value("${psm.video-service-url:${PSM_VIDEO_SERVICE_URL:http://psm-realtime:18087}}")
                                   String videoServiceUrl,
                                   @Value("${psm.work-permit-service-url:${PSM_WORK_PERMIT_SERVICE_URL:http://psm-operation:18088}}")
                                   String workPermitServiceUrl) {
        this.restTemplate = restTemplate;
        this.dualPreventionServiceUrl = RemoteClientSupport.trimTrailingSlash(
                dualPreventionServiceUrl, "http://psm-risk:18101");
        this.inspectionServiceUrl = RemoteClientSupport.trimTrailingSlash(
                inspectionServiceUrl, "http://psm-risk:18101");
        this.locationServiceUrl = RemoteClientSupport.trimTrailingSlash(
                locationServiceUrl, "http://psm-realtime:18087");
        this.videoServiceUrl = RemoteClientSupport.trimTrailingSlash(
                videoServiceUrl, "http://psm-realtime:18087");
        this.workPermitServiceUrl = RemoteClientSupport.trimTrailingSlash(
                workPermitServiceUrl, "http://psm-operation:18088");
    }

    @Override
    public Phase2ReportSummaryVO summary(Long tenantId) {
        requireTenantId(tenantId);
        Phase2ReportSummaryVO vo = new Phase2ReportSummaryVO();
        vo.setTenantId(tenantId);
        vo.setGeneratedAt(LocalDateTime.now());
        vo.setDataSource(DATA_SOURCE);
        vo.setDualPrevention(loadDualPrevention(tenantId));
        vo.setInspection(loadInspection(tenantId));
        vo.setLocation(loadLocation(tenantId));
        vo.setVideo(loadVideo(tenantId));
        vo.setSimops(loadSimops(tenantId));
        return vo;
    }

    private Phase2ReportSummaryVO.DualPreventionSection loadDualPrevention(Long tenantId) {
        Phase2ReportSummaryVO.DualPreventionSection section = new Phase2ReportSummaryVO.DualPreventionSection();
        Map<String, Object> stats = getMap(dualPreventionServiceUrl + "/api/dual-prevention/hazards/statistics?tenantId="
                + tenantId, "dual-prevention statistics");
        if (stats != null) {
            section.setTotalCount(longValue(stats.get("totalCount")));
            section.setOverdueCount(longValue(stats.get("overdueCount")));
            section.setClosedCount(longValue(stats.get("closedCount")));
        }
        return section;
    }

    private Phase2ReportSummaryVO.InspectionSection loadInspection(Long tenantId) {
        Phase2ReportSummaryVO.InspectionSection section = new Phase2ReportSummaryVO.InspectionSection();
        Map<String, Object> stats = getMap(inspectionServiceUrl + "/api/inspection/tasks/statistics?tenantId="
                + tenantId, "inspection statistics");
        if (stats != null) {
            section.setTotalCount(longValue(stats.get("totalCount")));
            section.setCompletedCount(longValue(stats.get("completedCount")));
            section.setMissedCount(longValue(stats.get("missedCount")));
            section.setAbnormalItemCount(longValue(stats.get("abnormalItemCount")));
        }
        return section;
    }

    private Phase2ReportSummaryVO.LocationSection loadLocation(Long tenantId) {
        Phase2ReportSummaryVO.LocationSection section = new Phase2ReportSummaryVO.LocationSection();
        PageResult<Map<String, Object>> page = getPage(locationServiceUrl + "/api/location/events?tenantId="
                + tenantId + "&pageNo=1&pageSize=1", "location events");
        if (page != null) {
            section.setEventCount(page.getTotal());
        }
        return section;
    }

    private Phase2ReportSummaryVO.VideoSection loadVideo(Long tenantId) {
        Phase2ReportSummaryVO.VideoSection section = new Phase2ReportSummaryVO.VideoSection();
        PageResult<Map<String, Object>> page = getPage(videoServiceUrl + "/api/video/ai-events?tenantId="
                + tenantId + "&pageNo=1&pageSize=1", "video ai events");
        if (page != null) {
            section.setAiEventCount(page.getTotal());
        }
        return section;
    }

    private Phase2ReportSummaryVO.SimopsSection loadSimops(Long tenantId) {
        Phase2ReportSummaryVO.SimopsSection section = new Phase2ReportSummaryVO.SimopsSection();
        Map<String, Object> stats = getMap(workPermitServiceUrl + "/api/simops/statistics?tenantId="
                + tenantId, "simops statistics");
        if (stats != null) {
            section.setTotalScans(longValue(stats.get("totalScans")));
            section.setTotalConflicts(longValue(stats.get("totalConflicts")));
            section.setBlockCount(longValue(stats.get("blockCount")));
            section.setCoordinateCount(longValue(stats.get("coordinateCount")));
            section.setWarnCount(longValue(stats.get("warnCount")));
        }
        return section;
    }

    private Map<String, Object> getMap(String url, String label) {
        try {
            ResponseEntity<ResponseVO<Map<String, Object>>> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, MAP_TYPE);
            return RemoteClientSupport.unwrap(response.getBody());
        } catch (RestClientException ex) {
            log.warn("phase2 {} failed url={} reason={}", label, url, ex.getMessage());
            return null;
        }
    }

    private PageResult<Map<String, Object>> getPage(String url, String label) {
        try {
            ResponseEntity<ResponseVO<PageResult<Map<String, Object>>>> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, PAGE_TYPE);
            return RemoteClientSupport.unwrap(response.getBody());
        } catch (RestClientException ex) {
            log.warn("phase2 {} failed url={} reason={}", label, url, ex.getMessage());
            return null;
        }
    }

    private long longValue(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    private void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is invalid");
        }
    }
}
