package com.fgroupboss.ai.psm.masterdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class MasterDataServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MasterDataServiceApplication.class, args);
    }
}

