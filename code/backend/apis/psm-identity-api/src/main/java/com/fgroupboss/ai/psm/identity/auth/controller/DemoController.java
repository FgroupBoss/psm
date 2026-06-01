package com.fgroupboss.ai.psm.identity.auth.controller;

import com.fgroupboss.ai.psm.common.DemoInfo;
import com.fgroupboss.ai.psm.common.ResponseVO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demo 模块 HTTP API。
 * <p>基础路径：{@code /api}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequestMapping("/api")
public class DemoController {

    @CrossOrigin
    /**
     * 查询demo。
     * <p>HTTP GET {@code /api/demo}</p>
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/demo")
    public ResponseVO<DemoInfo> demo() {
        DemoInfo info = new DemoInfo("psm-auth-service", "auth-demo", "1.0.0-SNAPSHOT");
        return ResponseVO.success(info);
    }
}
