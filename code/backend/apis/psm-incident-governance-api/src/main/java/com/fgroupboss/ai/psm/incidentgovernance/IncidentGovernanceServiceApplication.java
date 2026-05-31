package com.fgroupboss.ai.psm.incidentgovernance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 事件治理域服务 — 统一入口。
 * 合并了 incident + governance + report + integration。
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.fgroupboss.ai.psm.incidentgovernance",
        "com.fgroupboss.ai.psm.common"
})
public class IncidentGovernanceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(IncidentGovernanceServiceApplication.class, args);
    }
}
