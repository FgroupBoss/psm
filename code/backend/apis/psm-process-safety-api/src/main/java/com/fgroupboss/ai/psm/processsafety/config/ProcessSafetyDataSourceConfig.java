package com.fgroupboss.ai.psm.processsafety.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 过程安全域数据源配置 — 单库 psm_process_safety。
 */
@Configuration
@MapperScan(basePackages = {
        "com.fgroupboss.ai.psm.processsafety.pha.mapper",
        "com.fgroupboss.ai.psm.processsafety.moc.mapper",
        "com.fgroupboss.ai.psm.processsafety.pssr.mapper",
        "com.fgroupboss.ai.psm.processsafety.barrier.mapper",
        "com.fgroupboss.ai.psm.processsafety.configrule.mapper",
        "com.fgroupboss.ai.psm.processsafety.configrule.hotwork.mapper"
})
public class ProcessSafetyDataSourceConfig {
}
