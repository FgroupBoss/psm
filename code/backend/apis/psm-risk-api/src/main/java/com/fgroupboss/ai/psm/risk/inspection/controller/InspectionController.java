package com.fgroupboss.ai.psm.risk.inspection.controller;

import com.fgroupboss.ai.psm.common.DemoInfo;
import com.fgroupboss.ai.psm.common.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 巡检服务健康检查。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inspection")
public class InspectionController {

    @GetMapping("/health")
    public ResponseVO<DemoInfo> health() {
        return ResponseVO.success(new DemoInfo("psm-inspection-service", "inspection", "1.0.0-phase2"));
    }
}
