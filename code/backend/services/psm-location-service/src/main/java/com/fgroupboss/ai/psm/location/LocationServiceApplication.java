package com.fgroupboss.ai.psm.location;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.fgroupboss.ai.psm.location.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class LocationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocationServiceApplication.class, args);
    }
}
