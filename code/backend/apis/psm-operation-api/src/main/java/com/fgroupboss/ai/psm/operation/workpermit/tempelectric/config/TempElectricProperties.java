package com.fgroupboss.ai.psm.operation.workpermit.tempelectric.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "psm.temporary-electric")
public class TempElectricProperties {

    /** 是否启用临时用电专项规则 */
    private boolean enabled = false;

    /** 规则版本号 */
    private String ruleVersion = "TEMP-ELECTRIC-2026.05";

    /** 票证最长有效期（天） */
    private int maxValidDays = 7;
}
