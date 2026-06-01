package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ConfinedSpaceEntryRecordRequest {

    private Long tenantId;
    private Long workerId;
    private String workerName;
    private String action;
    private Date operatedAt;
    private String location;
}
