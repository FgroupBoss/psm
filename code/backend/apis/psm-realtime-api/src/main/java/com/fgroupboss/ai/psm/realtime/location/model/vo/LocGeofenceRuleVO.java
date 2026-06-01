package com.fgroupboss.ai.psm.realtime.location.model.vo;

import lombok.Data;

@Data
public class LocGeofenceRuleVO {

    private Long id;
    private Long fenceId;
    private String ruleType;
    private Integer thresholdValue;
    private Boolean enabled;
}
