package com.fgroupboss.ai.psm.operation.workpermit.excavation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "psm.excavation")
public class ExcavationProperties {

    /** 是否启用动土作业专项规则 */
    private boolean enabled = false;

    /** 规则版本号 */
    private String ruleVersion = "EXCAVATION-2026.05";

    /** 提交时必须完成会签的专业列表 */
    private List<String> requiredCountersignSpecialties = defaultSpecialties();

    private static List<String> defaultSpecialties() {
        List<String> list = new ArrayList<String>();
        list.add("ELECTRIC");
        list.add("PIPE");
        list.add("TELECOM");
        return list;
    }
}
