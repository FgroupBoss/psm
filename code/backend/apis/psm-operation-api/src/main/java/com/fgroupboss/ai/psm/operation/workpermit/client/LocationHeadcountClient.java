package com.fgroupboss.ai.psm.operation.workpermit.client;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.operation.workpermit.client.dto.LocationAreaHeadcountResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 调用 psm-location-service 区域在场人数（可选，失败时由调用方降级）。
 */
@Slf4j
@Component
public class LocationHeadcountClient {

    private static final ParameterizedTypeReference<ResponseVO<LocationAreaHeadcountResult>> RESPONSE_TYPE =
            new ParameterizedTypeReference<ResponseVO<LocationAreaHeadcountResult>>() {
            };

    private final RestTemplate restTemplate;
    private final String locationServiceUrl;

    public LocationHeadcountClient(RestTemplate restTemplate,
                                  @Value("${psm.location-service-url:${PSM_LOCATION_SERVICE_URL:http://localhost:18103}}")
                                  String locationServiceUrl) {
        this.restTemplate = restTemplate;
        this.locationServiceUrl = trimTrailingSlash(locationServiceUrl);
    }

    /**
     * 查询区域人数；定位服务不可用时返回 null，不抛异常。
     */
    public LocationAreaHeadcountResult headcountOptional(Long tenantId, Long areaId) {
        if (tenantId == null || areaId == null) {
            return null;
        }
        try {
            String url = locationServiceUrl + "/api/location/areas/" + areaId + "/headcount?tenantId=" + tenantId;
            ResponseEntity<ResponseVO<LocationAreaHeadcountResult>> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, RESPONSE_TYPE);
            return unwrap(response.getBody());
        } catch (RestClientException ex) {
            log.warn("location headcount unavailable areaId={} reason={}", areaId, ex.getMessage());
            return null;
        }
    }

    private <T> T unwrap(ResponseVO<T> body) {
        if (body == null || body.getCode() != 0) {
            return null;
        }
        return body.getData();
    }

    private String trimTrailingSlash(String url) {
        if (!StringUtils.hasText(url)) {
            return "http://localhost:18103";
        }
        String normalized = url.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
