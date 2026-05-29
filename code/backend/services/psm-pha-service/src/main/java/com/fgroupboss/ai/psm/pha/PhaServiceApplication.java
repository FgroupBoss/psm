package com.fgroupboss.ai.psm.pha;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.fgroupboss.ai.psm.pha.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class PhaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhaServiceApplication.class, args);
    }
}