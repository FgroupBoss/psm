package com.fgroupboss.ai.psm.operation.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 作业许可与管控域数据源配置 — 单库 psm_operation（work-permit + contractor）。
 */
@Configuration
@MapperScan(basePackages = {
        "com.fgroupboss.ai.psm.operation.workpermit.mapper",
        "com.fgroupboss.ai.psm.operation.workpermit.hotwork.mapper",
        "com.fgroupboss.ai.psm.operation.workpermit.heightwork.mapper",
        "com.fgroupboss.ai.psm.operation.workpermit.confinedspace.mapper",
        "com.fgroupboss.ai.psm.operation.workpermit.lifting.mapper",
        "com.fgroupboss.ai.psm.operation.workpermit.tempelectric.mapper",
        "com.fgroupboss.ai.psm.operation.workpermit.blindplate.mapper",
        "com.fgroupboss.ai.psm.operation.workpermit.excavation.mapper",
        "com.fgroupboss.ai.psm.operation.workpermit.roadbreak.mapper",
        "com.fgroupboss.ai.psm.operation.contractor.mapper"
})
public class OperationDataSourceConfig {
}
