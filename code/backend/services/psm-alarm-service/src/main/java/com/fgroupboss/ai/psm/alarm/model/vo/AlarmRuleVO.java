package com.fgroupboss.ai.psm.alarm.model.vo;

import lombok.Data;

@Data
public class AlarmRuleVO {

    private Long id;
    private Long tenantId;
    private String ruleCode;
    private String ruleName;
    private String ruleType;
    private String configJson;
    private String status;
}
