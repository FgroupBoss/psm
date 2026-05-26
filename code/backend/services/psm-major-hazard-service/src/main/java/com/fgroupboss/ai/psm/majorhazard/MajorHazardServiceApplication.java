package com.fgroupboss.ai.psm.majorhazard;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.fgroupboss.ai.psm.majorhazard.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class MajorHazardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MajorHazardServiceApplication.class, args);
    }
}

