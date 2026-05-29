package com.fgroupboss.ai.psm.moc.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * PHA 服务 HTTP 集成（URL 拼装桩，后续可扩展真实调用）。
 */
@Component
@RequiredArgsConstructor
public class PhaIntegrationClient {

    private final RestTemplate restTemplate;

    @Value("${psm.pha-service-url}")
    private String phaServiceUrl;

    public String buildReviewTriggerUrl(Long changeId) {
        return phaServiceUrl + "/api/pha/reviews/trigger?mocChangeId=" + changeId;
    }

    public String buildProjectUrl(Long projectId) {
        return phaServiceUrl + "/api/pha/projects/" + projectId;
    }
}
