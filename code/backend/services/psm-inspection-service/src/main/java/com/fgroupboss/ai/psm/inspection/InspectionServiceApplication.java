package com.fgroupboss.ai.psm.inspection;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.fgroupboss.ai.psm.inspection.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class InspectionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InspectionServiceApplication.class, args);
    }
}
