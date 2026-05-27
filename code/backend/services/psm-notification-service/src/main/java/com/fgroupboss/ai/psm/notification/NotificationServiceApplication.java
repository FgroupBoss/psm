package com.fgroupboss.ai.psm.notification;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.fgroupboss.ai.psm.notification.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
