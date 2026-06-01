package com.fgroupboss.ai.psm.operation.workpermit.confinedspace.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "psm.confined-space")
public class ConfinedSpaceProperties {

    /** 是否启用受限空间专项规则 */
    private boolean enabled = false;

    /** 规则版本号 */
    private String ruleVersion = "CONFINED-SPACE-2026.05";

    /** 票证最长有效期（天） */
    private int maxValidDays = 7;
}
