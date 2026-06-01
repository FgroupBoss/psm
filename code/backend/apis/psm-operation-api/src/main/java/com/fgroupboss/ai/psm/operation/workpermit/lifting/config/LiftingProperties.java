package com.fgroupboss.ai.psm.operation.workpermit.lifting.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "psm.lifting")
public class LiftingProperties {

    /** 是否启用吊装作业专项规则 */
    private boolean enabled = false;

    /** 规则版本号 */
    private String ruleVersion = "LIFTING-2026.05";

    /** 票证最长有效期（天） */
    private int maxValidDays = 7;
}
