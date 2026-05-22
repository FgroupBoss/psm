package com.fgroupboss.ai.psm.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class IntegrationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationServiceApplication.class, args);
    }
}

