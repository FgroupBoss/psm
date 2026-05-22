package com.fgroupboss.ai.psm.workpermit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class WorkPermitServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkPermitServiceApplication.class, args);
    }
}

