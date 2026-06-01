package com.fgroupboss.ai.psm.operation.workpermit.blindplate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "psm.blind-plate")
public class BlindPlateProperties {

    /** 是否启用盲板抽堵专项规则 */
    private boolean enabled = false;

    /** 规则版本号 */
    private String ruleVersion = "BLIND-PLATE-2026.05";
}
