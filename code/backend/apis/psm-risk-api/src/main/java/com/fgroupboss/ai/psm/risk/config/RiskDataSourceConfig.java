package com.fgroupboss.ai.psm.risk.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 风险防控域数据源配置 — 单库 psm_risk（dual-prevention + inspection + major-hazard）。
 */
@Configuration
@MapperScan(basePackages = {
        "com.fgroupboss.ai.psm.risk.dualprevention.mapper",
        "com.fgroupboss.ai.psm.risk.majorhazard.mapper",
        "com.fgroupboss.ai.psm.risk.inspection.mapper"
})
public class RiskDataSourceConfig {
}
