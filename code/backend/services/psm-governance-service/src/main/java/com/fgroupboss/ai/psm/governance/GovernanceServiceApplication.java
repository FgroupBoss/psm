package com.fgroupboss.ai.psm.governance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 集团治理与数据仓库服务启动类。
 */
@MapperScan("com.fgroupboss.ai.psm.governance.mapper")
@SpringBootApplication(scanBasePackages = "com.fgroupboss.ai.psm")
public class GovernanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GovernanceServiceApplication.class, args);
    }
}
