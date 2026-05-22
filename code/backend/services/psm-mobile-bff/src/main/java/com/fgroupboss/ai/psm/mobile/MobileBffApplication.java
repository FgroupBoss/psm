package com.fgroupboss.ai.psm.mobile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class MobileBffApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobileBffApplication.class, args);
    }
}

