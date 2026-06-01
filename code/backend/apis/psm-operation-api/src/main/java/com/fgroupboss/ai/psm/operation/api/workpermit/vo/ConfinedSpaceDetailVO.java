package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ConfinedSpaceDetailVO {

    private Long id;
    private Long workPermitId;
    private Long spaceId;
    private String spaceName;
    private Integer entryCount;
    private String ventilationType;
    private Boolean continuousMonitoring;
    private String ruleVersion;
    private Date validUntil;
}
