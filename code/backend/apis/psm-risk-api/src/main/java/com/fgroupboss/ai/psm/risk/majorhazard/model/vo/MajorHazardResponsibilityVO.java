package com.fgroupboss.ai.psm.risk.majorhazard.model.vo;

import lombok.Data;

@Data
public class MajorHazardResponsibilityVO {

    private Long id;
    private Long tenantId;
    private Long hazardId;
    private String responsibilityType;
    private String personName;
    private String personPhone;
    private Long personId;
    private Integer sortNo;
}
