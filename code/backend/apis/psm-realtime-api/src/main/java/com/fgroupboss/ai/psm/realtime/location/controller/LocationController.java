package com.fgroupboss.ai.psm.realtime.location.controller;

import com.fgroupboss.ai.psm.common.DemoInfo;
import com.fgroupboss.ai.psm.common.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定位服务健康检查。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location")
public class LocationController {

    @GetMapping("/health")
    public ResponseVO<DemoInfo> health() {
        return ResponseVO.success(new DemoInfo("psm-location-service", "location", "1.0.0-phase2"));
    }
}
