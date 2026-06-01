package com.fgroupboss.ai.psm.operation.workpermit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "psm.hot-work.multi-node-approval")
public class HotWorkApprovalProperties {

    /**
     * 是否启用动火多节点或签/会签审批。
     */
    private boolean enabled = false;
}
