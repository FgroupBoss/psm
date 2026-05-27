package com.fgroupboss.ai.psm.alarm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.fgroupboss.ai.psm.alarm.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class AlarmServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlarmServiceApplication.class, args);
    }
}
