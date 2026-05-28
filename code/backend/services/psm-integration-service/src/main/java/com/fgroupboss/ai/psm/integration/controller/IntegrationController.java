package com.fgroupboss.ai.psm.integration.controller;

import com.fgroupboss.ai.psm.common.DemoInfo;
import com.fgroupboss.ai.psm.common.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 集成服务健康检查。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/integration")
public class IntegrationController {

    @GetMapping("/health")
    public ResponseVO<DemoInfo> health() {
        return ResponseVO.success(new DemoInfo("psm-integration-service", "integration", "1.0.0-phase2"));
    }
}
