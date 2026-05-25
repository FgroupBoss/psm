package com.fgroupboss.ai.psm.auth.controller;

import com.fgroupboss.ai.psm.common.DemoInfo;
import com.fgroupboss.ai.psm.common.ResponseVO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides the existing integration smoke-check endpoint for this service.
 */
@RestController
@RequestMapping("/api")
public class DemoController {

    @CrossOrigin
    @GetMapping("/demo")
    public ResponseVO<DemoInfo> demo() {
        DemoInfo info = new DemoInfo("psm-auth-service", "auth-demo", "1.0.0-SNAPSHOT");
        return ResponseVO.success(info);
    }
}
