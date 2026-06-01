package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ConfinedSpaceEntryRecordVO {

    private Long id;
    private Long workerId;
    private String workerName;
    private String action;
    private Date operatedAt;
    private String location;
    private String operator;
}
