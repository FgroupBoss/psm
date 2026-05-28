package com.fgroupboss.ai.psm.dualprevention;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.fgroupboss.ai.psm.dualprevention.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class DualPreventionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DualPreventionServiceApplication.class, args);
    }
}
