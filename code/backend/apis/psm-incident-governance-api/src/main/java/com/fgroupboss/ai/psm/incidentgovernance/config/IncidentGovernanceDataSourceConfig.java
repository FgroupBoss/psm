package com.fgroupboss.ai.psm.incidentgovernance.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 事件治理域数据源配置 — 单库 psm_incident_governance。
 */
@Configuration
@MapperScan(basePackages = {
        "com.fgroupboss.ai.psm.incidentgovernance.incident.mapper",
        "com.fgroupboss.ai.psm.incidentgovernance.governance.mapper",
        "com.fgroupboss.ai.psm.incidentgovernance.report.mapper",
        "com.fgroupboss.ai.psm.incidentgovernance.integration.mapper"
})
public class IncidentGovernanceDataSourceConfig {
}
