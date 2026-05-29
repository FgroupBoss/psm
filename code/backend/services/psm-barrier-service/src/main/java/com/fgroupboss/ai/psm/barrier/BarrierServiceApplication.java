package com.fgroupboss.ai.psm.barrier;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.fgroupboss.ai.psm.barrier.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class BarrierServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BarrierServiceApplication.class, args);
    }
}
