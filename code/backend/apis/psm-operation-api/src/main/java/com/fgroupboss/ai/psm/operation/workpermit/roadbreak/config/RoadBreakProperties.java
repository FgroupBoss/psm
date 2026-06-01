package com.fgroupboss.ai.psm.operation.workpermit.roadbreak.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "psm.road-break")
public class RoadBreakProperties {

    /** 是否启用断路作业专项规则 */
    private boolean enabled = false;

    /** 规则版本号 */
    private String ruleVersion = "ROAD-BREAK-2026.05";
}
