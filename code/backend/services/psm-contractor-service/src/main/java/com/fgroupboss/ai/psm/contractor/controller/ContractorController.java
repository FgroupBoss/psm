package com.fgroupboss.ai.psm.contractor.controller;

import com.fgroupboss.ai.psm.common.DemoInfo;
import com.fgroupboss.ai.psm.common.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 承包商服务健康检查。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contractors")
public class ContractorController {

    /**
     * 接口用途：查询服务健康状态。
     */
    @GetMapping("/health")
    public ResponseVO<DemoInfo> health() {
        return ResponseVO.success(new DemoInfo("psm-contractor-service", "contractor", "1.0.0-batch3"));
    }
}
