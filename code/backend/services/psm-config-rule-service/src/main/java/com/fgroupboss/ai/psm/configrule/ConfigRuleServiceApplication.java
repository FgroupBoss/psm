package com.fgroupboss.ai.psm.configrule;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.fgroupboss.ai.psm.configrule.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class ConfigRuleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigRuleServiceApplication.class, args);
    }
}
