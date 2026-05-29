package com.fgroupboss.ai.psm.pssr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.fgroupboss.ai.psm.pssr.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class PssrServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PssrServiceApplication.class, args);
    }
}
