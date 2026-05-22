package com.fgroupboss.ai.psm.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class IamServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IamServiceApplication.class, args);
    }
}

