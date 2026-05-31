package com.fgroupboss.ai.psm.risk.dualprevention.controller;

import com.fgroupboss.ai.psm.common.DemoInfo;
import com.fgroupboss.ai.psm.common.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 双重预防服务健康检查。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dual-prevention")
public class DualPreventionController {

    @GetMapping("/health")
    public ResponseVO<DemoInfo> health() {
        return ResponseVO.success(new DemoInfo("psm-dual-prevention-service", "dual-prevention", "1.0.0-phase2"));
    }
}
