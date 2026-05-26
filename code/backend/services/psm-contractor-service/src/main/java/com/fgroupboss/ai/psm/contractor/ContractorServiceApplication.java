package com.fgroupboss.ai.psm.contractor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.fgroupboss.ai.psm.contractor.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class ContractorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContractorServiceApplication.class, args);
    }
}

