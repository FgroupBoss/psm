package com.fgroupboss.ai.psm.moc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@MapperScan("com.fgroupboss.ai.psm.moc.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class MocServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MocServiceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
