package com.fgroupboss.ai.psm.alarm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class AlarmServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlarmServiceApplication.class, args);
    }
}

