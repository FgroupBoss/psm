package com.fgroupboss.ai.psm.workpermit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.fgroupboss.ai.psm.workpermit.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class WorkPermitServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkPermitServiceApplication.class, args);
    }
}
