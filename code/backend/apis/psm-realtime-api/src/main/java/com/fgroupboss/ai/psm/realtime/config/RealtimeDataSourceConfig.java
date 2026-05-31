package com.fgroupboss.ai.psm.realtime.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 实时感知域数据源配置 — 单库 psm_realtime（alarm + location + video）。
 */
@Configuration
@MapperScan(basePackages = {
        "com.fgroupboss.ai.psm.realtime.alarm.mapper",
        "com.fgroupboss.ai.psm.realtime.location.mapper",
        "com.fgroupboss.ai.psm.realtime.video.mapper"
})
public class RealtimeDataSourceConfig {
}
