package com.fgroupboss.ai.psm.mobile.client;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.mobile.client.dto.AcceptanceRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.CheckInRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.GasTestRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.MobileDraftSyncRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.MonitorRecordRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.PermitActionRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.SafetyMeasureRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.SitePermitRequest;
import com.fgroupboss.ai.psm.mobile.client.vo.GasTestVO;
import com.fgroupboss.ai.psm.mobile.client.vo.MobileDraftSyncResultVO;
import com.fgroupboss.ai.psm.mobile.client.vo.MonitorRecordVO;
import com.fgroupboss.ai.psm.mobile.client.vo.SafetyMeasureVO;
import com.fgroupboss.ai.psm.mobile.client.vo.SiteConfirmVO;
import com.fgroupboss.ai.psm.mobile.client.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.mobile.client.vo.WorkPermitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * 调用 psm-work-permit-service 作业票接口。
 */
@Slf4j
@Component
public class WorkPermitClient {

    private static final ParameterizedTypeReference<ResponseVO<PageResult<WorkPermitVO>>> PAGE_TYPE =
            new ParameterizedTypeReference<ResponseVO<PageResult<WorkPermitVO>>>() {
            };

    private static final ParameterizedTypeReference<ResponseVO<WorkPermitDetailVO>> DETAIL_TYPE =
            new ParameterizedTypeReference<ResponseVO<WorkPermitDetailVO>>() {
            };

    private static final ParameterizedTypeReference<ResponseVO<WorkPermitVO>> PERMIT_TYPE =
            new ParameterizedTypeReference<ResponseVO<WorkPermitVO>>() {
            };

    private static final ParameterizedTypeReference<ResponseVO<SiteConfirmVO>> CHECK_IN_TYPE =
            new ParameterizedTypeReference<ResponseVO<SiteConfirmVO>>() {
            };

    private static final ParameterizedTypeReference<ResponseVO<GasTestVO>> GAS_TEST_TYPE =
            new ParameterizedTypeReference<ResponseVO<GasTestVO>>() {
            };

    private static final ParameterizedTypeReference<ResponseVO<SafetyMeasureVO>> MEASURE_TYPE =
            new ParameterizedTypeReference<ResponseVO<SafetyMeasureVO>>() {
            };

    private static final ParameterizedTypeReference<ResponseVO<MonitorRecordVO>> MONITOR_TYPE =
            new ParameterizedTypeReference<ResponseVO<MonitorRecordVO>>() {
            };

    private static final ParameterizedTypeReference<ResponseVO<MobileDraftSyncResultVO>> DRAFT_TYPE =
            new ParameterizedTypeReference<ResponseVO<MobileDraftSyncResultVO>>() {
            };

    private final RestTemplate restTemplate;
    private final String workPermitServiceUrl;

    public WorkPermitClient(RestTemplate restTemplate,
                            @Value("${psm.work-permit-service-url:${PSM_WORK_PERMIT_SERVICE_URL:http://localhost:18088}}")
                            String workPermitServiceUrl) {
        this.restTemplate = restTemplate;
        this.workPermitServiceUrl = trimTrailingSlash(workPermitServiceUrl);
    }

    public List<WorkPermitVO> page(Long tenantId, String status, int pageNo, int pageSize) {
        if (tenantId == null) {
            return Collections.emptyList();
        }
        String url = workPermitServiceUrl + "/api/work-permits?tenantId=" + tenantId
                + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
        if (StringUtils.hasText(status)) {
            url += "&status=" + status;
        }
        ResponseEntity<ResponseVO<PageResult<WorkPermitVO>>> response =
                restTemplate.exchange(url, HttpMethod.GET, null, PAGE_TYPE);
        PageResult<WorkPermitVO> page = requireData(response.getBody(), "work permit page");
        if (page == null || page.getRecords() == null) {
            return Collections.emptyList();
        }
        return page.getRecords();
    }

    public WorkPermitDetailVO getDetail(Long tenantId, Long id) {
        String url = workPermitServiceUrl + "/api/work-permits/" + id + "?tenantId=" + tenantId;
        ResponseEntity<ResponseVO<WorkPermitDetailVO>> response =
                restTemplate.exchange(url, HttpMethod.GET, null, DETAIL_TYPE);
        return requireData(response.getBody(), "work permit detail");
    }

    public SiteConfirmVO checkIn(Long id, CheckInRequest request, HttpHeaders contextHeaders) {
        String url = workPermitServiceUrl + "/api/work-permits/" + id + "/check-in";
        return post(url, request, contextHeaders, CHECK_IN_TYPE, "check-in");
    }

    public GasTestVO addGasTest(Long id, GasTestRequest request, HttpHeaders contextHeaders) {
        String url = workPermitServiceUrl + "/api/work-permits/" + id + "/gas-tests";
        return post(url, request, contextHeaders, GAS_TEST_TYPE, "gas-test");
    }

    public SafetyMeasureVO confirmSafetyMeasure(Long id, Long measureId, SafetyMeasureRequest request,
                                                HttpHeaders contextHeaders) {
        String url = workPermitServiceUrl + "/api/work-permits/" + id + "/safety-measures/" + measureId;
        return post(url, request, contextHeaders, MEASURE_TYPE, "safety-measure");
    }

    public WorkPermitVO sitePermit(Long id, SitePermitRequest request, HttpHeaders contextHeaders) {
        String url = workPermitServiceUrl + "/api/work-permits/" + id + "/site-permit";
        return post(url, request, contextHeaders, PERMIT_TYPE, "site-permit");
    }

    public MonitorRecordVO addMonitorRecord(Long id, MonitorRecordRequest request, HttpHeaders contextHeaders) {
        String url = workPermitServiceUrl + "/api/work-permits/" + id + "/monitor-records";
        return post(url, request, contextHeaders, MONITOR_TYPE, "monitor-record");
    }

    public WorkPermitVO suspend(Long tenantId, Long id, PermitActionRequest request, HttpHeaders contextHeaders) {
        String url = workPermitServiceUrl + "/api/work-permits/" + id + "/suspend?tenantId=" + tenantId;
        return post(url, request, contextHeaders, PERMIT_TYPE, "suspend");
    }

    public WorkPermitVO resume(Long tenantId, Long id, PermitActionRequest request, HttpHeaders contextHeaders) {
        String url = workPermitServiceUrl + "/api/work-permits/" + id + "/resume?tenantId=" + tenantId;
        return post(url, request, contextHeaders, PERMIT_TYPE, "resume");
    }

    public WorkPermitVO acceptance(Long id, AcceptanceRequest request, HttpHeaders contextHeaders) {
        String url = workPermitServiceUrl + "/api/work-permits/" + id + "/acceptance";
        return post(url, request, contextHeaders, PERMIT_TYPE, "acceptance");
    }

    /**
     * 尝试同步草稿至 work-permit-service；404 或连接失败时返回 empty 由 BFF 本地兜底。
     */
    public java.util.Optional<MobileDraftSyncResultVO> syncDraft(MobileDraftSyncRequest request,
                                                                 HttpHeaders contextHeaders) {
        String url = workPermitServiceUrl + "/api/work-permits/mobile/drafts/sync";
        try {
            HttpEntity<MobileDraftSyncRequest> entity = new HttpEntity<MobileDraftSyncRequest>(request, contextHeaders);
            ResponseEntity<ResponseVO<MobileDraftSyncResultVO>> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, DRAFT_TYPE);
            MobileDraftSyncResultVO result = requireData(response.getBody(), "draft sync");
            return java.util.Optional.of(result);
        } catch (HttpStatusCodeException ex) {
            if (ex.getRawStatusCode() == 404) {
                log.debug("work-permit draft sync endpoint not found, fallback to local storage");
                return java.util.Optional.empty();
            }
            log.warn("work-permit draft sync failed status={} reason={}", ex.getRawStatusCode(), ex.getMessage());
            return java.util.Optional.empty();
        } catch (RestClientException ex) {
            log.warn("work-permit draft sync unavailable reason={}", ex.getMessage());
            return java.util.Optional.empty();
        }
    }

    public static HttpHeaders buildContextHeaders(String userId, String username, String tenantId) {
        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.hasText(userId)) {
            headers.set(UserContextHeaders.USER_ID, userId);
        }
        if (StringUtils.hasText(username)) {
            headers.set(UserContextHeaders.USERNAME, username);
        }
        if (StringUtils.hasText(tenantId)) {
            headers.set(UserContextHeaders.TENANT_ID, tenantId);
        }
        return headers;
    }

    private <T> T post(String url, Object body, HttpHeaders contextHeaders,
                       ParameterizedTypeReference<ResponseVO<T>> type, String action) {
        HttpEntity<Object> entity = new HttpEntity<Object>(body, contextHeaders);
        try {
            ResponseEntity<ResponseVO<T>> response = restTemplate.exchange(url, HttpMethod.POST, entity, type);
            return requireData(response.getBody(), action);
        } catch (HttpStatusCodeException ex) {
            log.warn("work-permit {} failed status={} body={}", action, ex.getRawStatusCode(), ex.getResponseBodyAsString());
            throw new BusinessException(ex.getRawStatusCode(), "work-permit " + action + " failed");
        } catch (RestClientException ex) {
            log.error("work-permit {} call error", action, ex);
            throw new BusinessException(502, "work-permit service unavailable");
        }
    }

    private <T> T requireData(ResponseVO<T> body, String action) {
        if (body == null) {
            throw new BusinessException(502, "work-permit " + action + " returned empty body");
        }
        if (body.getCode() != 0) {
            throw new BusinessException(body.getCode(), body.getMessage());
        }
        return body.getData();
    }

    private String trimTrailingSlash(String url) {
        if (!StringUtils.hasText(url)) {
            return "http://localhost:18088";
        }
        String normalized = url.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
