package com.fgroupboss.ai.psm.risk.dualprevention.controller;

import com.fgroupboss.ai.psm.common.DemoInfo;
import com.fgroupboss.ai.psm.common.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * DualPrevention 模块 HTTP API。
 * <p>双重预防机制：风险单元、事件与管控措施。</p>
 * <p>基础路径：{@code /api/dual-prevention}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dual-prevention")
public class DualPreventionController {

    /**
     * 服务健康检查。
     * <p>HTTP GET {@code /api/dual-prevention/health}</p>
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/health")
    public ResponseVO<DemoInfo> health() {
        return ResponseVO.success(new DemoInfo("psm-dual-prevention-service", "dual-prevention", "1.0.0-phase2"));
    }
}
