package com.fgroupboss.ai.psm.operation.workpermit.client;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.operation.client.HotWorkWorkflowApi;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkApproverResolveDTO;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkApproverResolveResultDTO;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkWorkflowQueryDTO;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkWorkflowSnapshotDTO;
import com.fgroupboss.ai.psm.operation.client.dto.hotwork.HotWorkWorkflowSummaryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * 调用 process-safety 动火审批流配置接口。
 */
@Component
public class HotWorkWorkflowClient implements HotWorkWorkflowApi {

    private static final ParameterizedTypeReference<ResponseVO<List<HotWorkWorkflowSummaryDTO>>> LIST_TYPE =
            new ParameterizedTypeReference<ResponseVO<List<HotWorkWorkflowSummaryDTO>>>() {
            };
    private static final ParameterizedTypeReference<ResponseVO<HotWorkWorkflowSnapshotDTO>> SNAPSHOT_TYPE =
            new ParameterizedTypeReference<ResponseVO<HotWorkWorkflowSnapshotDTO>>() {
            };
    private static final ParameterizedTypeReference<ResponseVO<HotWorkApproverResolveResultDTO>> RESOLVE_TYPE =
            new ParameterizedTypeReference<ResponseVO<HotWorkApproverResolveResultDTO>>() {
            };

    private final RestTemplate restTemplate;
    private final String processSafetyServiceUrl;

    public HotWorkWorkflowClient(RestTemplate restTemplate,
                                 @Value("${psm.process-safety-service-url:${PSM_PROCESS_SAFETY_SERVICE_URL:http://psm-process-safety:18111}}")
                                 String processSafetyServiceUrl) {
        this.restTemplate = restTemplate;
        this.processSafetyServiceUrl = trimTrailingSlash(processSafetyServiceUrl);
    }

    @Override
    public List<HotWorkWorkflowSummaryDTO> listAvailable(HotWorkWorkflowQueryDTO query) {
        String url = UriComponentsBuilder.fromHttpUrl(processSafetyServiceUrl + "/api/config/hot-work/workflows/available")
                .queryParam("tenantId", query.getTenantId())
                .queryParam("hotWorkLevel", query.getHotWorkLevel())
                .queryParam("areaId", query.getAreaId())
                .queryParam("status", query.getStatus())
                .build(true)
                .toUriString();
        ResponseEntity<ResponseVO<List<HotWorkWorkflowSummaryDTO>>> response =
                restTemplate.exchange(url, HttpMethod.GET, null, LIST_TYPE);
        List<HotWorkWorkflowSummaryDTO> data = unwrap(response.getBody());
        return data == null ? Arrays.asList() : data;
    }

    @Override
    public HotWorkWorkflowSnapshotDTO getSnapshot(Long tenantId, Long templateId, Integer versionNo) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(processSafetyServiceUrl + "/api/config/hot-work/workflows/" + templateId + "/snapshot")
                .queryParam("tenantId", tenantId);
        if (versionNo != null) {
            builder.queryParam("versionNo", versionNo);
        }
        ResponseEntity<ResponseVO<HotWorkWorkflowSnapshotDTO>> response =
                restTemplate.exchange(builder.build(true).toUriString(), HttpMethod.GET, null, SNAPSHOT_TYPE);
        HotWorkWorkflowSnapshotDTO data = unwrap(response.getBody());
        if (data == null) {
            throw new RestClientException("hot-work snapshot returned empty body");
        }
        return data;
    }

    @Override
    public HotWorkApproverResolveResultDTO resolveApprovers(HotWorkApproverResolveDTO request) {
        String url = processSafetyServiceUrl + "/api/config/hot-work/workflows/resolve-approvers";
        HttpEntity<HotWorkApproverResolveDTO> entity = new HttpEntity<HotWorkApproverResolveDTO>(request);
        ResponseEntity<ResponseVO<HotWorkApproverResolveResultDTO>> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, RESOLVE_TYPE);
        HotWorkApproverResolveResultDTO data = unwrap(response.getBody());
        if (data == null) {
            throw new RestClientException("resolve-approvers returned empty body");
        }
        return data;
    }

    private <T> T unwrap(ResponseVO<T> body) {
        if (body == null || body.getCode() != 0) {
            return null;
        }
        return body.getData();
    }

    private String trimTrailingSlash(String url) {
        if (!StringUtils.hasText(url)) {
            return "http://psm-process-safety:18111";
        }
        String normalized = url.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
