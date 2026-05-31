package com.fgroupboss.ai.psm.risk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 风险防控域服务 — 统一入口。
 * 合并了 major-hazard + inspection + dual-prevention。
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.fgroupboss.ai.psm.risk",
        "com.fgroupboss.ai.psm.common"
})
public class RiskControlServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RiskControlServiceApplication.class, args);
    }
}
