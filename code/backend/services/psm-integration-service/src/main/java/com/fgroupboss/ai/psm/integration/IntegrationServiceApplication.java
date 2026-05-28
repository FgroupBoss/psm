package com.fgroupboss.ai.psm.integration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.fgroupboss.ai.psm.integration.mapper")
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class IntegrationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationServiceApplication.class, args);
    }
}

