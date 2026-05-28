package com.fgroupboss.ai.psm.video;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.fgroupboss.ai.psm.video.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class VideoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoServiceApplication.class, args);
    }
}
