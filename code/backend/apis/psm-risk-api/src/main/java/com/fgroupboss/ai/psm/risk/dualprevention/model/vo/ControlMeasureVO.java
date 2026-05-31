package com.fgroupboss.ai.psm.risk.dualprevention.model.vo;

import lombok.Data;

@Data
public class ControlMeasureVO {

    private Long id;
    private Long tenantId;
    private Long riskEventId;
    private String measureType;
    private String measureContent;
    private String responsiblePost;
    private Integer checkCycleDays;
    private String status;
}
