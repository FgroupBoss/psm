package com.fgroupboss.ai.psm.moc.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * PSSR 服务 HTTP 集成（URL 拼装桩，后续可扩展真实调用）。
 */
@Component
@RequiredArgsConstructor
public class PssrIntegrationClient {

    private final RestTemplate restTemplate;

    @Value("${psm.pssr-service-url}")
    private String pssrServiceUrl;

    public String buildPssrTriggerUrl(Long changeId) {
        return pssrServiceUrl + "/api/pssr/projects/trigger?mocChangeId=" + changeId;
    }
}
