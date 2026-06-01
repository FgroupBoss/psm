package com.fgroupboss.ai.psm.operation.workpermit.heightwork.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "psm.height-work")
public class HeightWorkProperties {

    /** 是否启用高处作业专项规则 */
    private boolean enabled = false;

    /** 是否启用天气校验（不可用时转人工复核） */
    private boolean weatherCheckEnabled = false;

    /** 天气服务不可用时是否允许人工复核 */
    private boolean weatherManualReviewEnabled = true;

    /** 规则版本号，写入详情与归档快照 */
    private String ruleVersion = "HEIGHT-WORK-2026.05";

    /** 票证最长有效期（天） */
    private int maxValidDays = 7;
}
