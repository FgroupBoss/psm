package com.fgroupboss.ai.psm.identity.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 身份与接入域数据源配置 — 单库 psm_identity。
 */
@Configuration
@MapperScan(basePackages = {
        "com.fgroupboss.ai.psm.identity.auth.mapper",
        "com.fgroupboss.ai.psm.identity.iam.mapper",
        "com.fgroupboss.ai.psm.identity.audit.mapper",
        "com.fgroupboss.ai.psm.identity.masterdata.mapper",
        "com.fgroupboss.ai.psm.identity.file.mapper",
        "com.fgroupboss.ai.psm.identity.notification.mapper"
})
public class IdentityDataSourceConfig {
}
