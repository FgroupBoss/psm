package com.fgroupboss.ai.psm.realtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 实时感知域服务 — 统一入口。
 * 合并了 alarm + location + video。
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.fgroupboss.ai.psm.realtime",
        "com.fgroupboss.ai.psm.common"
})
public class RealtimePerceptionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RealtimePerceptionServiceApplication.class, args);
    }
}
