package com.fgroupboss.ai.psm.processsafety;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 过程安全域服务 — 统一入口。
 * 合并了 pha + moc + pssr + barrier + config-rule。
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.fgroupboss.ai.psm.processsafety",
        "com.fgroupboss.ai.psm.common"
})
public class ProcessSafetyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProcessSafetyServiceApplication.class, args);
    }
}
